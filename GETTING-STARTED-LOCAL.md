# Getting Started (local)

Como levantar la app en tu maquina con Docker.

## Requisitos

- Docker + Docker Compose.
- JDK 21 solo si queres compilar fuera de Docker (no hace falta para correrla).

## Levantar

Desde la raiz del repo:

```bash
docker compose up -d --build
```

Esto levanta MySQL, Adminer y la app. Espera a que el contenedor `franchise-app`
quede sano (la app espera a que MySQL este listo antes de arrancar).

## URLs

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Adminer (cliente web de la BD): `http://localhost:8001`
  - Server: `mysql`
  - Usuario: `root`
  - Password: `root`
  - Base de datos: `franchise`

## Datos de ejemplo

La base arranca con datos cargados por el seeder (`data.sql`), asi que podes probar
los casos de uso sin tener que cargar nada a mano. Vienen 2 franquicias, 3 sucursales
y 8 productos con distinto stock. El seeder solo carga si las tablas estan vacias, asi
que reiniciar no duplica datos.

## Probar

Algunos ejemplos:

```bash
# Producto de mayor stock por sucursal de la franquicia 1
curl http://localhost:8080/api/franchise/1/top-stock-product

# Crear un producto en la sucursal 1
curl -X POST http://localhost:8080/api/branch/1/product \
  -H "Content-Type: application/json" \
  -d '{"name":"Flat White","stock":45}'
```

## Parar

```bash
docker compose down       # frena los contenedores
docker compose down -v    # ademas borra los datos de la BD
```

## Tests

```bash
./gradlew test -x pitest
```
