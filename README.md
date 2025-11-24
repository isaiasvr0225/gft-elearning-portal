# GFT E‑Learning Portal

Portal de e‑learning compuesto por un backend en Spring Boot, un frontend en Angular y una base de datos PostgreSQL, todo orquestado con Docker Compose.

## Arquitectura

- Base de datos: PostgreSQL 15 (contenedor `elearning-db`)
- Backend: Spring Boot (contenedor `elearning-back`)
  - Expone la API en el puerto 8080
  - Se conecta a PostgreSQL mediante variables de entorno
  - Healthcheck: `GET /actuator/health`
- Frontend: Angular compilado y servido con Nginx (contenedor `elearning-front`)
  - Sirve la aplicación en el puerto 80

Red y volúmenes:
- Red bridge `elearning-network` para la comunicación entre servicios
- Volumen persistente `elearning-db-data` para los datos de PostgreSQL

## Requisitos previos

- Docker Desktop (Windows/Mac) o Docker Engine + Docker Compose
- No es necesario tener Node ni Java instalados localmente para ejecutar con Docker

## Estructura del proyecto

```
./
├─ docker-compose.yml
├─ gft-elearning-back/      # Backend Spring Boot + Dockerfile
└─ gft-elearning-front/     # Frontend Angular + Dockerfile
```

## Cómo levantar el proyecto con Docker Compose

Todos los comandos se ejecutan desde la carpeta raíz del proyecto.

1) Construir las imágenes (opcional, `up` también construye si hace falta):

- PowerShell / CMD:
```
docker compose build
```

2) Levantar los servicios en segundo plano:
```
docker compose up -d
```

3) Verificar estado de los contenedores:
```
docker compose ps
```

4) Acceder a las aplicaciones:
- Frontend (Nginx): http://localhost/
- Backend (API): http://localhost:8080/
- Health backend: http://localhost:8080/actuator/health

5) Ver logs (útiles para depurar):
```
docker compose logs -f elearning-back
```
```
docker compose logs -f elearning-front
```
```
docker compose logs -f elearning-db
```

6) Detener servicios (mantiene datos del volumen):
```
docker compose down
```

7) Detener y borrar volumen de datos (ATENCIÓN: elimina la BD):
```
docker compose down -v
```

## Variables y puertos por defecto

- PostgreSQL (`elearning-db`)
  - POSTGRES_DB=elearning
  - POSTGRES_USER=postgres
  - POSTGRES_PASSWORD=postgres
  - Puerto host: 5432
- Backend (`elearning-back`)
  - SPRING_DATASOURCE_URL=jdbc:postgresql://elearning-db:5432/elearning
  - SPRING_DATASOURCE_USERNAME=postgres
  - SPRING_DATASOURCE_PASSWORD=postgres
  - SERVER_PORT=8080 (mapeado a 8080 del host)
- Frontend (`elearning-front`)
  - Servido por Nginx en el puerto 80 del contenedor (mapeado a 80 del host)

Puedes ajustar puertos o credenciales editando `docker-compose.yml`.

## Flujo de construcción

- Frontend: se construye con Node 20 (etapa build) y se copia el artefacto estático a una imagen de Nginx.
  - Salida Angular: `dist/gft-elearning/browser` según `angular.json`.
- Backend: se construye con el Dockerfile del módulo `gft-elearning-back` (no se requiere Maven local).

## Solución de problemas

- El frontend no se ve en http://localhost/:
  - Revisa logs: `docker compose logs -f elearning-front`.
  - Asegúrate de haber reconstruido tras cambios: `docker compose build elearning-front && docker compose up -d`.
- El backend marca unhealthy:
  - Espera a que la BD esté healthy (Compose ya lo orquesta).
  - Revisa `http://localhost:8080/actuator/health` y logs del backend: `docker compose logs -f elearning-back`.
- Conflictos de puertos:
  - Cambia los mapeos `"80:80"`, `"8080:8080"`, `"5432:5432"` en `docker-compose.yml` si ya están en uso.

## Desarrollo local (opcional)

Si prefieres ejecutar servicios por separado sin Docker, consulta los README dentro de cada módulo (no requerido para levantar con Docker).

## Licencia

Este proyecto es de uso interno/educativo. Ajusta la sección de licencia según las políticas de tu organización.
