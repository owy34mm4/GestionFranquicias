# Despliegue en AWS (Terraform + GitHub Actions)

Infraestructura como codigo para desplegar la app en AWS. La entrada es un
**API Gateway (HTTP)** que va por un **VPC Link** a un **ALB interno**, y el ALB
reparte a la tarea **ECS Fargate**, que a su vez habla con **RDS MySQL**. Tambien
hay ECR (registro de imagenes), CloudWatch Logs y un rol de IAM para la tarea.
El estado remoto se guarda en S3 con bloqueo en DynamoDB.

Flujo de una peticion:

```
Cliente -> API Gateway (HTTPS) -> VPC Link -> ALB interno (:80) -> ECS Fargate (:8080) -> RDS
```

La unica puerta publica es el API Gateway. El ALB es interno (no se accede
directo desde internet) y la tarea solo acepta trafico del ALB.

## Estructura modular

Cada elemento de infra vive en su propio modulo local dentro de
`deployment/terraform/modules/`, y el root (`main.tf`) los conecta:

| modulo | que crea |
| ---------- | -------------------------------------------------------- |
| network | lee la VPC default y sus subnets |
| ecr | repositorio de imagenes (modulo oficial) |
| iam | rol de ejecucion de la tarea ECS (modulo oficial) |
| logs | log group de CloudWatch (modulo oficial) |
| rds | base MySQL + su security group (modulo oficial) |
| ecs | cluster + service Fargate detras del ALB (modulo oficial) |
| alb | ALB interno + target group + listener (modulo oficial) |
| apigateway | HTTP API + VPC Link al ALB (modulo oficial) |

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

4. **Aplicar Terraform**: crea toda la infraestructura.

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
   `aws ecs wait services-stable` hasta que la tarea este corriendo detras del ALB.
7. **Obtener la URL de la app**: es la URL del API Gateway. Sale al final del log
   del workflow (`App is available at: https://...`) o con el output
   `api_gateway_url` de Terraform. Es estable (no cambia entre despliegues).

## Los workflows solo funcionan desde `main`

GitHub Actions ejecuta los workflows tal como estan en la rama `main`. Recien
cuando `cd-deploy.yml` y `terraform.yml` esten fusionados en `main`:

- el push a `main` va a disparar **Deploy to ECS**, y
- el **Terraform** workflow (manual) va a aparecer en la pestana Actions.

## Nota sobre la version de MySQL (8.4)

La configuracion usa MySQL 8.4 (`engine_version = "8.4"`, `family = "mysql8.4"`,
`major_engine_version = "8.4"`). Si la cuenta o la region no ofrecen 8.4, el
plan alternativo es bajar a 8.0: `engine_version = "8.0"`,
`family = "mysql8.0"`, `major_engine_version = "8.0"`.

## Nota sobre las versiones de los modulos

Los modulos oficiales estan fijados a versiones que funcionan con el proveedor
AWS `~> 5.0` (algunas versiones nuevas piden el proveedor v6):

- `terraform-aws-modules/ecr` -> `~> 2.4` (la 3.x pide proveedor v6).
- `terraform-aws-modules/rds` -> `~> 6.13` (desde la v7 quitaron la variable
  `password` en favor de un valor efimero, y nosotros la pasamos como variable).
- `terraform-aws-modules/alb` -> `~> 9.17` (la v10 pide proveedor v6).
- `terraform-aws-modules/apigateway-v2` -> `~> 5.5`.
- `terraform-aws-modules/security-group` -> `~> 5.3` (para el SG del VPC Link).
