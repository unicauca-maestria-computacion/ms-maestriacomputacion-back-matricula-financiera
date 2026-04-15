# HLD-01: High-Level Design — ms-matricula-financiera

## 1. Contexto del Sistema (C4 Level 1)

```
┌─────────────────────────────────────────────────────────────────┐
│                        Sistema Maestría                          │
│                                                                  │
│  ┌──────────────┐    REST     ┌──────────────────────────────┐  │
│  │   Frontend   │ ──────────► │  ms-matricula-financiera     │  │
│  │  Angular     │             │  (puerto 8092)               │  │
│  └──────────────┘             └──────────────┬───────────────┘  │
│                                              │                   │
│                               ┌─────────────▼──────────────┐   │
│                               │  BD Compartida MySQL        │   │
│                               │  maestria-computacion       │   │
│                               │  (tablas: estudiantes,      │   │
│                               │   matriculas, cursos,       │   │
│                               │   periodo_academico,        │   │
│                               │   personas, asignaturas)    │   │
│                               └────────────────────────────┘   │
│                                                                  │
│  Nota: todos los datos (estudiantes, matrículas, periodos)      │
│  se obtienen exclusivamente de la BD compartida.                 │
│  No hay dependencia de otros microservicios en tiempo de         │
│  ejecución normal.                                               │
└─────────────────────────────────────────────────────────────────┘
```

## 2. Contenedores (C4 Level 2)

```
ms-matricula-financiera
├── REST API (Spring Boot 3.x, puerto 8092)
│   └── Expone: /api/v1/gestion-matricula-financiera/**
└── Persistence Adapter
    └── Accede a BD compartida MySQL via JPA + JdbcTemplate
        (estudiantes, matriculas, cursos, periodo_academico, personas, asignaturas)
```

## 3. Decisiones de Arquitectura (ADRs)

### ADR-01: BD Compartida como única fuente de datos
**Decisión:** Todos los datos (estudiantes, matrículas, periodos académicos, personas) se obtienen exclusivamente de la BD compartida MySQL.
**Razón:** La BD compartida es la fuente de verdad. No existe dependencia de otros microservicios en tiempo de ejecución.
**Consecuencia:** Se usa `JdbcTemplate` para queries complejos multi-tabla. No se requiere cliente HTTP externo para la operación normal del servicio.

### ADR-02: Arquitectura Hexagonal estricta
**Decisión:** Ports en `domain/ports/`, use cases en `application/usecases/`, sin Spring en dominio.
**Razón:** Testabilidad, separación de concerns, cumplimiento del steering `hexagonal-springboot.md`.
**Consecuencia:** `BeanConfig` es el único lugar de wiring. Use cases son POJOs puros.

### ADR-03: MapStruct para mappers
**Decisión:** Reemplazar mappers manuales por MapStruct.
**Razón:** Eliminar código boilerplate, reducir errores de mapeo, mejorar mantenibilidad.
**Consecuencia:** Agregar dependencias MapStruct al pom.xml con binding de Lombok.

### ADR-04: Migración a application.yml con perfiles
**Decisión:** Reemplazar `application.properties` por `application.yml` con perfiles dev/prod.
**Razón:** Credenciales hardcodeadas son un riesgo de seguridad. Variables de entorno son el estándar.
**Consecuencia:** Requiere configurar variables de entorno en despliegue.

### ADR-05: Nomenclatura en inglés para código
**Decisión:** Renombrar clases, métodos y paquetes al inglés siguiendo el steering.
**Razón:** Consistencia con estándares del equipo y steering `hexagonal-springboot.md`.
**Consecuencia:** Refactorización de nombres en toda la base de código.

## 4. Estrategia NFR

### Disponibilidad
- El servicio funciona aunque `ms-matricula-academica` esté caído (BD compartida como primaria).
- `try/catch` en todos los clientes externos con logging y retorno de lista vacía.

### Seguridad
- Perfil `dev`: sin autenticación (permitAll).
- Perfil `prod`: OAuth2 JWT, rol `ROLE_MF_READ` para todos los endpoints.
- `SessionCreationPolicy.STATELESS` siempre.

### Observabilidad
- Spring Boot Actuator en `/actuator/health` y `/actuator/info`.
- Logging estructurado con SLF4J en todos los adapters externos.
- Errores HTTP con `ProblemDetail` (RFC 7807).

### Mantenibilidad
- ArchUnit tests verifican que el dominio no tenga dependencias de infraestructura.
- Cobertura mínima 80% en `domain/` y `application/`.
