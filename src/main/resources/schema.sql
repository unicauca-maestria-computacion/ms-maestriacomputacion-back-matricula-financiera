-- =============================================================
-- Script de microservicios: Matrícula Financiera + Matrícula Académica
-- Todas las tablas usan IF NOT EXISTS para poder ejecutarse en
-- cualquier orden junto con otros schemas del proyecto.
-- =============================================================

CREATE TABLE IF NOT EXISTS personas (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    identificacion       BIGINT       UNIQUE,
    nombre               VARCHAR(255),
    apellido             VARCHAR(255),
    correo_electronico   VARCHAR(255) UNIQUE,
    telefono             VARCHAR(255),
    genero               VARCHAR(255),
    tipo_identificacion  VARCHAR(255),
    CONSTRAINT pk_personas PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS periodo_academico (
    id                  BIGINT      NOT NULL AUTO_INCREMENT,
    tag_periodo         INT         NOT NULL,
    fecha_inicio        DATE        NOT NULL,
    fecha_fin           DATE        NOT NULL,
    fecha_fin_matricula DATE        NOT NULL,
    descripcion         VARCHAR(255),
    estado              VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT pk_periodo_academico PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS asignaturas (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    codigo_asignatura   BIGINT       UNIQUE,
    nombre_asignatura   VARCHAR(255) UNIQUE,
    estado_asignatura   TINYINT,
    area_formacion      INT,
    tipo_asignatura     VARCHAR(255),
    creditos            INT,
    CONSTRAINT pk_asignaturas PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS estudiantes (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    id_persona           BIGINT       NULL DEFAULT NULL,
    codigo               VARCHAR(255) UNIQUE NULL DEFAULT NULL,
    cohorte              INT          NULL DEFAULT NULL,
    periodo_ingreso      VARCHAR(255) NULL DEFAULT NULL,
    semestre_financiero  INT          NULL DEFAULT NULL,
    semestre_academico   INT          NULL DEFAULT NULL,
    estado_maestria      VARCHAR(255) NULL DEFAULT NULL,
    modalidad            VARCHAR(255) NULL DEFAULT NULL,
    modalidad_ingreso    VARCHAR(255) NULL DEFAULT NULL,
    titulo_doctorado     VARCHAR(255) NULL DEFAULT NULL,
    ciudad_residencia    VARCHAR(255) NULL DEFAULT NULL,
    correo_universidad   VARCHAR(255) UNIQUE NULL DEFAULT NULL,
    fecha_grado          DATE         NULL DEFAULT NULL,
    titulo_pregrado      VARCHAR(255) NULL DEFAULT NULL,
    observacion          VARCHAR(255) NULL DEFAULT NULL,
    CONSTRAINT pk_estudiantes PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS docentes (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    id_persona    BIGINT       NULL DEFAULT NULL,
    codigo        VARCHAR(255),
    facultad      VARCHAR(255),
    departamento  VARCHAR(255),
    estado        VARCHAR(50),
    CONSTRAINT pk_docentes PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cursos (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    grupocurso       VARCHAR(20)   NOT NULL,
    periodo_id       BIGINT        NOT NULL,
    id_asignatura    BIGINT        NOT NULL,
    horariocurso     VARCHAR(100)  NOT NULL,
    saloncurso       VARCHAR(50)   NOT NULL,
    observacioncurso VARCHAR(255)  NULL,
    estado           TINYINT       NOT NULL DEFAULT 1,
    CONSTRAINT pk_cursos PRIMARY KEY (id),
    CONSTRAINT uk_curso_grupo_periodo_asignatura UNIQUE (grupocurso, periodo_id, id_asignatura),
    CONSTRAINT fk_cursos_periodo    FOREIGN KEY (periodo_id)    REFERENCES periodo_academico (id),
    CONSTRAINT fk_cursos_asignatura FOREIGN KEY (id_asignatura) REFERENCES asignaturas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS curso_docente (
    id_curso    BIGINT NOT NULL,
    id_docente  BIGINT NOT NULL,
    CONSTRAINT pk_curso_docente PRIMARY KEY (id_curso, id_docente),
    CONSTRAINT fk_cd_curso   FOREIGN KEY (id_curso)   REFERENCES cursos (id),
    CONSTRAINT fk_cd_docente FOREIGN KEY (id_docente) REFERENCES docentes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS matriculas (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    id_estudiante    BIGINT,
    id_curso         BIGINT,
    id_periodo       BIGINT,
    estado           INT,
    estado_matricula VARCHAR(50),
    observacion      VARCHAR(255),
    CONSTRAINT pk_matriculas PRIMARY KEY (id),
    CONSTRAINT fk_mat_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiantes (id),
    CONSTRAINT fk_mat_curso      FOREIGN KEY (id_curso)      REFERENCES cursos (id),
    CONSTRAINT fk_mat_periodo    FOREIGN KEY (id_periodo)    REFERENCES periodo_academico (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS areas_formacion (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(255),
    descripcion VARCHAR(255),
    CONSTRAINT pk_areas_formacion PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS docentes_asignatura (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    id_docente    BIGINT NOT NULL,
    id_asignatura BIGINT NOT NULL,
    CONSTRAINT pk_docentes_asignatura PRIMARY KEY (id),
    CONSTRAINT uk_docente_asignatura  UNIQUE (id_docente, id_asignatura),
    CONSTRAINT fk_da_docente    FOREIGN KEY (id_docente)    REFERENCES docentes (id),
    CONSTRAINT fk_da_asignatura FOREIGN KEY (id_asignatura) REFERENCES asignaturas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS materiales_apoyo (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    nombrematerial      VARCHAR(150) NOT NULL UNIQUE,
    descripcionmaterial VARCHAR(500),
    enlacesmaterial     VARCHAR(500) NOT NULL,
    CONSTRAINT pk_materiales_apoyo PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS curso_material (
    id_curso    BIGINT NOT NULL,
    id_material BIGINT NOT NULL,
    CONSTRAINT pk_curso_material PRIMARY KEY (id_curso, id_material),
    CONSTRAINT fk_cm_curso    FOREIGN KEY (id_curso)    REFERENCES cursos (id),
    CONSTRAINT fk_cm_material FOREIGN KEY (id_material) REFERENCES materiales_apoyo (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS matricula_calificaciones (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    id_matricula  BIGINT      NOT NULL,
    id_asignatura BIGINT      NOT NULL,
    nota          DECIMAL(5,2),
    es_definitiva TINYINT,
    CONSTRAINT pk_matricula_calificaciones PRIMARY KEY (id),
    CONSTRAINT fk_mc_matricula  FOREIGN KEY (id_matricula)  REFERENCES matriculas (id),
    CONSTRAINT fk_mc_asignatura FOREIGN KEY (id_asignatura) REFERENCES asignaturas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS docente_estudiante (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    id_docente    BIGINT NOT NULL,
    id_estudiante BIGINT NOT NULL,
    tipo          VARCHAR(255),
    CONSTRAINT pk_docente_estudiante PRIMARY KEY (id),
    CONSTRAINT uk_docente_estudiante UNIQUE (id_docente, id_estudiante),
    CONSTRAINT fk_de_docente    FOREIGN KEY (id_docente)    REFERENCES docentes (id),
    CONSTRAINT fk_de_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiantes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
