# Despliegue en AWS (Terraform + GitHub Actions)

Esta carpeta contiene la infraestructura como codigo para desplegar la app en
AWS: RDS MySQL, ECR, ECS Fargate (sin ALB, se accede por la IP publica de la
tarea), CloudWatch Logs y un rol de IAM para la ejecucion de la tarea ECS.
El estado remoto se guarda en S3 con bloqueo en DynamoDB.

## Secretos de GitHub necesarios

Configuralos en el repositorio (Settings -> Secrets and variables -> Actions):

| Secreto                 | Para que sirve                                  |
| ----------------------- | ----------------------------------------------- |
| `AWS_ACCESS_KEY_ID`     | Access key del usuario IAM de despliegue        |
| `AWS_SECRET_ACCESS_KEY` | Secret key de ese mismo usuario IAM             |
| `DB_PASSWORD`           | Password del usuario maestro de la base de datos |

Ningun valor de secreto esta escrito en el codigo. La password llega a
Terraform como `TF_VAR_db_password` y a la tarea ECS como variable de entorno.

## Orden exacto de puesta en marcha

1. **Crear el usuario IAM y sus claves**: crea un usuario IAM con permisos de
   despliegue y genera un access key + secret key.
2. **Cargar los secretos en GitHub**: guarda `AWS_ACCESS_KEY_ID`,
   `AWS_SECRET_ACCESS_KEY` y `DB_PASSWORD` como secretos del repositorio.
3. **Ejecutar `bootstrap.sh` una sola vez**: crea el bucket S3 y la tabla
   DynamoDB del backend. Es seguro volver a correrlo (ignora lo que ya existe).

   ```bash
   cd deployment/terraform
   ./bootstrap.sh
   ```

4. **Aplicar Terraform**: crea toda la infraestructura. Se puede hacer en local

   ```bash
   cd deployment/terraform
   terraform init
   export TF_VAR_db_password="<tu-password>"
   terraform apply
   ```

   o usando el workflow **Terraform** (Actions -> Terraform -> Run workflow ->
   `apply`).
5. **Hacer push a `main`**: el workflow **Deploy to ECS** construye la imagen,
   la sube a ECR y fuerza un nuevo despliegue del servicio. El primer push es el
   que sube la primera imagen.
6. **Esperar a que el servicio quede sano**: el workflow espera con
   `aws ecs wait services-stable` hasta que la tarea este corriendo.
7. **Obtener la IP publica**: mirala al final del log del workflow
   (`App is available at: http://<ip>:8080`) o con el output `public_ip_command`
   de Terraform, que imprime los comandos de AWS CLI para resolverla.

## Aviso: la IP publica es efimera

No hay ALB ni Elastic IP. La IP publica de la tarea Fargate **cambia en cada
despliegue**, por eso no se expone como un valor fijo: se obtiene en tiempo de
ejecucion con el output `public_ip_command` o desde el log del CD. Si necesitas
una direccion estable, habria que agregar un ALB o una Elastic IP.

## Los workflows solo funcionan desde `main`

GitHub Actions ejecuta los workflows tal como estan en la rama `main`. Mientras
`cd-deploy.yml` y `terraform.yml` vivan solo en `feature/terraform-deploy` no se
van a disparar. Recien cuando esten fusionados en `main`:

- el push a `main` va a disparar **Deploy to ECS**, y
- el **Terraform** workflow (manual) va a aparecer en la pestana Actions.

## Nota sobre la version de MySQL (8.4)

La configuracion usa MySQL 8.4 (`engine_version = "8.4"`, `family = "mysql8.4"`,
`major_engine_version = "8.4"`). Si la cuenta o la region no ofrecen 8.4, el
plan alternativo es bajar a 8.0: `engine_version = "8.0"`,
`family = "mysql8.0"`, `major_engine_version = "8.0"`.

## Nota sobre las versiones de los modulos

- ECR: `terraform-aws-modules/ecr/aws` fijado en `~> 2.4`. La linea 3.x del
  modulo requiere el proveedor AWS v6, pero aca usamos el proveedor v5.
- RDS: `terraform-aws-modules/rds/aws` fijado en `~> 6.13`. Desde la v7 el modulo
  quito la variable `password` en favor de un valor efimero `password_wo`;
  nosotros pasamos la password como variable sensible normal, asi que la 6.13.x
  es la adecuada.
