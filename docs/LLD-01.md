# LLD-01: Low-Level Design — ms-matricula-financiera

## 1. Estructura de Paquetes Target

```
co.edu.unicauca.matricula_financiera/
├── MatriculaFinancieraApplication.java
├── domain/
│   ├── model/
│   │   ├── Estudiante.java
│   │   ├── MatriculaFinanciera.java
│   │   ├── MatriculaAcademica.java
│   │   ├── PeriodoAcademico.java
│   │   ├── Beca.java
│   │   ├── Descuento.java
│   │   ├── Materia.java
│   │   └── Docente.java
│   ├── ports/
│   │   ├── in/
│   │   │   └── ManageEnrolledStudentsUseCase.java
│   │   └── out/
│   │       ├── StudentGatewayPort.java
│   │       └── ResultFormatterPort.java
│   └── enums/
│       └── PeriodoEstado.java
├── application/
│   └── usecases/
│       └── ManageEnrolledStudentsUseCaseImpl.java
├── infrastructure/
│   ├── in/
│   │   └── rest/
│   │       └── student/
│   │           ├── controller/
│   │           │   └── StudentController.java
│   │           ├── dtoRequest/
│   │           │   └── PeriodoAcademicoRequest.java
│   │           ├── dtoResponse/
│   │           │   ├── StudentResponse.java
│   │           │   ├── PeriodoAcademicoResponse.java
│   │           │   ├── BecaResponse.java
│   │           │   ├── DescuentoResponse.java
│   │           │   ├── MateriaResponse.java
│   │           │   └── DocenteResponse.java
│   │           └── mapper/
│   │               └── StudentHttpMapper.java
│   └── out/
│       ├── persistence/
│       │   ├── entity/
│       │   │   └── EstudianteEntity.java
│       │   ├── repository/
│       │   │   ├── EstudianteJpaRepository.java
│       │   │   └── BdCompartidaRepository.java
│       │   ├── mapper/
│       │   │   └── EstudiantePersistenceMapper.java
│       │   └── gateway/
│       │       └── StudentGatewayAdapter.java
│       └── formatter/
│           └── ResultFormatterAdapter.java
└── config/
    ├── beans/
    │   └── BeanConfig.java
    ├── security/
    │   └── SecurityConfig.java
    └── exceptions/
        ├── GlobalExceptionHandler.java
        ├── structure/
        │   └── ErrorCode.java
        └── custom/
            ├── BaseException.java
            ├── EntityNotFoundException.java
            ├── EntityAlreadyExistsException.java
            ├── BusinessRuleViolatedException.java
            └── InvalidRequestDataException.java
```

## 2. Domain Model — Firmas

```java
// domain/ports/in/ManageEnrolledStudentsUseCase.java
public interface ManageEnrolledStudentsUseCase {
    List<Estudiante> getStudentsByPeriod(PeriodoAcademico period);
    Estudiante getStudentByCode(String code, Integer tagPeriodo, Integer year);
    List<PeriodoAcademico> getAcademicPeriods();
}

// domain/ports/out/StudentGatewayPort.java
public interface StudentGatewayPort {
    List<Estudiante> findStudentsByPeriodId(Long periodId);
    List<Estudiante> findStudentsByIds(List<Long> ids);
    Estudiante findStudentByCode(String code);
    boolean existsStudentByCode(String code);
    void enrichPersonalData(Estudiante student);
    boolean existsAcademicPeriod(PeriodoAcademico period);
    PeriodoAcademico findPeriodByTagAndYear(Integer tag, Integer year);
    List<PeriodoAcademico> findAllPeriods();
    PeriodoAcademico findActivePeriod();
    List<MatriculaAcademica> findAcademicEnrollments(Long studentId, Integer tag, Integer year);
}
public interface ResultFormatterPort {
    void errorEntityNotFound(String messageKey, Object... args);
    void errorEntityAlreadyExists(String messageKey, Object... args);
    void errorBusinessRuleViolated(String messageKey, Object... args);
    void errorInvalidRequestData(String messageKey, Object... args);
    void errorInternalFailure(String messageKey, Object... args);
}
```

## 3. Use Case — Flujo de Orquestación

```java
// application/usecases/ManageEnrolledStudentsUseCaseImpl.java
@RequiredArgsConstructor
public class ManageEnrolledStudentsUseCaseImpl implements ManageEnrolledStudentsUseCase {
    private final StudentGatewayPort gateway;
    private final ResultFormatterPort formatter;

    @Override
    public List<Estudiante> getStudentsByPeriod(PeriodoAcademico period) {
        // 1. Validar nulo → formatter.errorBusinessRuleViolated(...)
        // 2. Verificar existencia → gateway.existsAcademicPeriod(period)
        //    Si no existe → formatter.errorEntityNotFound(...)
        // 3. Resolver ID → gateway.findPeriodByTagAndYear(tag, year)
        // 4. Obtener estudiantes → gateway.findStudentsByPeriodId(id)
        // 5. Para cada estudiante → enrich(student, tag, year)
        // 6. Retornar lista
    }

    @Override
    public Estudiante getStudentByCode(String code, Integer tag, Integer year) {
        // 1. Validar nulo → formatter.errorBusinessRuleViolated(...)
        // 2. Verificar existencia → gateway.existsStudentByCode(code)
        //    Si no existe → formatter.errorEntityNotFound(...)
        // 3. Obtener → gateway.findStudentByCode(code)
        // 4. Resolver periodo (parámetro o activo)
        // 5. Enriquecer → enrich(student, tag, year)
        // 6. Retornar
    }

    private void enrich(Estudiante student, Integer tag, Integer year) {
        gateway.enrichPersonalData(student);
        List<MatriculaAcademica> enrollments =
            gateway.findAcademicEnrollments(student.getId(), tag, year);
        student.setMatriculasAcademicas(enrollments);
        student.setValorEnSMLV(calculateSmlv(student));
    }

    private Integer calculateSmlv(Estudiante student) {
        // Semestres 1-4 → 6
        // Semestre ≥ 5, sin materias → null
        // Semestre ≥ 5, solo TG2 → 1
        // Semestre ≥ 5, otras materias → 6
    }
}
```

## 4. REST Controller — Endpoints

| Método | Path | Request | Response | HTTP |
|--------|------|---------|----------|------|
| POST | `/api/v1/gestion-matricula-financiera/estudiantes` | `PeriodoAcademicoRequest` | `List<StudentResponse>` | 200 |
| GET | `/api/v1/gestion-matricula-financiera/estudiantes/{codigo}` | path + query params | `StudentResponse` | 200 |
| GET | `/api/v1/gestion-matricula-financiera/periodos` | — | `List<PeriodoAcademicoResponse>` | 200 |

## 5. DTOs — Firmas

```java
// dtoRequest/PeriodoAcademicoRequest.java
public class PeriodoAcademicoRequest {
    @NotNull(message = "{validation.periodo.tagPeriodo.notNull}")
    private Integer tagPeriodo;

    @NotNull(message = "{validation.periodo.anio.notNull}")
    private Integer anio;
}

// dtoResponse/StudentResponse.java
public class StudentResponse {
    private String codigo;
    private String nombre;
    private String apellido;
    private Long identificacion;
    private Integer cohorte;
    private String periodoIngreso;
    private Integer semestreFinanciero;
    private Integer semestreAcademico;
    private Integer valorEnSMLV;
    private List<DescuentoResponse> descuentos;
    private List<BecaResponse> becas;
    private List<MateriaResponse> materias;
}

// dtoResponse/MateriaResponse.java
public class MateriaResponse {
    private String codigoOid;
    private String materia;
    private Integer creditos;
    private String tipo;
    private String grupoClase;
    private String horario;
    private String salon;
    private String estadoMatricula;
    private String observacion;
    private DocenteResponse docente;
}
```

## 6. Esquema de BD (tablas relevantes para este microservicio)

```sql
-- Tabla propia del microservicio financiero
CREATE TABLE estudiantes (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_persona          BIGINT NULL,           -- FK a personas (BD compartida)
    codigo              VARCHAR(255) UNIQUE,
    cohorte             INT,
    periodo_ingreso     VARCHAR(255),
    semestre_financiero INT,
    semestre_academico  INT
);

-- Tablas compartidas (solo lectura desde este microservicio)
-- periodo_academico, personas, matriculas, cursos, asignaturas, docentes
```

## 7. Mappers MapStruct

```java
// infrastructure/in/rest/student/mapper/StudentHttpMapper.java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentHttpMapper {
    StudentResponse fromEstudianteToResponse(Estudiante estudiante);
    List<StudentResponse> fromListToResponse(List<Estudiante> estudiantes);
    PeriodoAcademico fromRequestToPeriodo(PeriodoAcademicoRequest request);
    PeriodoAcademicoResponse fromPeriodoToResponse(PeriodoAcademico periodo);
}

// infrastructure/out/persistence/mapper/EstudiantePersistenceMapper.java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EstudiantePersistenceMapper {
    Estudiante fromEntityToEstudiante(EstudianteEntity entity);
    List<Estudiante> fromListEntityToEstudiante(List<EstudianteEntity> entities);
    EstudianteEntity fromEstudianteToEntity(Estudiante estudiante);
}
```

## 8. ErrorCode Enum

```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    GENERIC_ERROR          ("MF-0001", "error.generic"),
    ENTITY_ALREADY_EXISTS  ("MF-0002", "error.entity.exists"),
    ENTITY_NOT_FOUND       ("MF-0003", "error.entity.notFound"),
    DENIED_STATE           ("MF-0004", "error.state.denied"),
    BUSINESS_RULE_VIOLATED ("MF-0005", "error.businessRule.violated"),
    INVALID_REQUEST_DATA   ("MF-0006", "error.request.invalid");

    private final String code;
    private final String messageKey;
}
```

## 9. Diagrama de Secuencia — getStudentsByPeriod

```
Frontend → StudentController.getStudents(PeriodoAcademicoRequest)
  → StudentHttpMapper.fromRequestToPeriodo(request)
  → ManageEnrolledStudentsUseCase.getStudentsByPeriod(period)
    → StudentGatewayPort.existsAcademicPeriod(period)
      → BdCompartidaRepository.findPeriodoByTagAndAnio(tag, year)
    → StudentGatewayPort.findPeriodByTagAndYear(tag, year)
    → StudentGatewayPort.findStudentsByPeriodId(periodId)
      → EstudianteJpaRepository.findByPeriodoId(periodId)
    → [for each student]
      → StudentGatewayPort.enrichPersonalData(student)
        → BdCompartidaRepository.findDatosPersonalesEstudiante(id)
      → StudentGatewayPort.findAcademicEnrollments(id, tag, year)
        → BdCompartidaRepository.findMatriculasPorEstudianteYPeriodo(...)
  → StudentHttpMapper.fromListToResponse(students)
  → ResponseEntity.ok(studentsDTO)
```
