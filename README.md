# Gestion de Franquicias

API REST reactiva para administrar franquicias. Una franquicia tiene sucursales
y cada sucursal tiene productos con stock.

## Stack

- Java 21
- Spring Boot (WebFlux, endpoints funcionales)
- R2DBC + MySQL
- Gradle
- Docker / Docker Compose

## Arquitectura

El proyecto usa Clean Architecture (scaffold de Bancolombia), separado en modulos:

- `domain/model` - entidades del dominio y puertos (interfaces de repositorio).
- `domain/usecase` - los casos de uso (logica de negocio).
- `infrastructure/entry-points/reactive-web` - la API (router + handlers funcionales).
- `infrastructure/driven-adapters/r2dbc-mysql` - persistencia con R2DBC.
- `applications/app-service` - arranca la app y arma todo.

La idea es que el dominio no conozca detalles de infraestructura: los casos de uso
hablan con puertos y los adaptadores los implementan.

## Como correr

En local con Docker. Los pasos estan en [GETTING-STARTED-LOCAL.md](GETTING-STARTED-LOCAL.md).

## Endpoints

Public URL = https://5mcyan1x7g.execute-api.us-east-1.amazonaws.com/swagger-ui/index.html#/

Todas las rutas cuelgan de `/api`.

| Metodo | Ruta                                             | Que hace                                        |
| ------ | ------------------------------------------------ | ----------------------------------------------- |
| POST   | `/api/franchise`                                 | Crea una franquicia                             |
| POST   | `/api/franchise/{franchiseId}/branch`            | Agrega una sucursal a una franquicia            |
| POST   | `/api/branch/{branchId}/product`                 | Agrega un producto a una sucursal               |
| DELETE | `/api/branch/{branchId}/product/{productId}`     | Elimina un producto de una sucursal             |
| PATCH  | `/api/branch/{branchId}/product/{productId}/stock` | Actualiza el stock de un producto             |
| GET    | `/api/franchise/{franchiseId}/top-stock-product` | Producto de mayor stock por cada sucursal       |
| PATCH  | `/api/franchise/{franchiseId}/name`              | Renombra una franquicia                         |
| PATCH  | `/api/branch/{branchId}/name`                    | Renombra una sucursal                           |
| PATCH  | `/api/branch/{branchId}/product/{productId}/name` | Renombra un producto                           |

Los bodies y respuestas estan documentados en Swagger.

## Documentacion de la API

Con la app corriendo, Swagger UI queda en `http://localhost:8080/swagger-ui.html`.

## Tests

```bash
./gradlew test -x pitest
```

## Despliegue

El despliegue en AWS (Terraform + GitHub Actions) esta explicado en
[deployment/terraform/README.md](deployment/terraform/README.md).
