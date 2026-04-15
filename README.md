# ms-matricula-financiera

Microservicio de gestión de matrícula financiera para la Maestría en Computación — Universidad del Cauca.

## Descripción

Calcula y expone el valor en SMLV de cada estudiante matriculado por periodo académico. Accede a una base de datos compartida MySQL para obtener datos de estudiantes, matrículas, cursos y periodos.

## Tecnologías

- Java 17
- Spring Boot 3.x
- Spring Data JPA + JdbcTemplate
- MySQL 8
- Lombok + MapStruct
- Maven

## Arquitectura

Hexagonal (Ports & Adapters). Ver [HLD-01.md](docs/HLD-01.md) y [LLD-01.md](docs/LLD-01.md).

```
domain/          → Modelos y puertos (sin Spring, sin JPA)
application/     → Casos de uso (orquestación pura)
infrastructure/  → Adapters REST, persistencia, cliente externo
config/          → Wiring, seguridad, excepciones
```

## Requisitos Previos

- Java 17+
- Maven 3.8+
- MySQL 8 corriendo en `localhost:3306`
- Base de datos `maestria-computacion` creada con el schema en `src/main/resources/schema.sql`

## Variables de Entorno

| Variable | Descripción | Ejemplo |
|---|---|---|
| `DB_URL` | URL de conexión a MySQL | `jdbc:mysql://localhost:3306/maestria-computacion` |
| `DB_USERNAME` | Usuario de BD | `root` |
| `DB_PASSWORD` | Contraseña de BD | `secret` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `dev` o `prod` |

## Cómo Ejecutar Localmente

```bash
# 1. Clonar y entrar al directorio
cd ms-maestriacomputacion-back-matricula-financiera

# 2. Configurar variables de entorno (perfil dev no requiere auth)
export DB_URL=jdbc:mysql://localhost:3306/maestria-computacion
export DB_USERNAME=root
export DB_PASSWORD=root
export SPRING_PROFILES_ACTIVE=dev

# 3. Compilar y ejecutar
./mvnw spring-boot:run
```

El servicio queda disponible en `http://localhost:8092`.

## Endpoints

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/gestion-matricula-financiera/estudiantes` | Estudiantes por periodo |
| GET | `/api/v1/gestion-matricula-financiera/estudiantes/{codigo}` | Estudiante por código |
| GET | `/api/v1/gestion-matricula-financiera/periodos` | Periodos académicos |
| GET | `/actuator/health` | Health check |

Ver especificación completa en [docs/OPENAPI.yml](docs/OPENAPI.yml).

## Documentación

| Documento | Descripción |
|---|---|
| [docs/HU-01.md](docs/HU-01.md) | HU: Consultar estudiantes por periodo |
| [docs/HU-02.md](docs/HU-02.md) | HU: Consultar estudiante por código |
| [docs/HU-03.md](docs/HU-03.md) | HU: Consultar periodos académicos |
| [docs/HLD-01.md](docs/HLD-01.md) | High-Level Design |
| [docs/LLD-01.md](docs/LLD-01.md) | Low-Level Design |
| [docs/OPENAPI.yml](docs/OPENAPI.yml) | Contrato OpenAPI 3.0 |

## Reglas de Negocio — Cálculo SMLV

| Condición | Valor SMLV |
|---|---|
| Semestre financiero 1–4 | 6 |
| Semestre ≥ 5, sin materias en el periodo | null |
| Semestre ≥ 5, solo Trabajo de Grado 2 | 1 |
| Semestre ≥ 5, con otras materias | 6 |

## Ejecutar Tests

```bash
./mvnw test
```

## Construir imagen Docker

```bash
./mvnw package -DskipTests
docker build -t ms-matricula-financiera:latest .
```
