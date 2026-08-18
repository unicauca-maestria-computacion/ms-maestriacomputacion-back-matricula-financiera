-- =============================================================
-- Esquema minimo requerido por las pruebas de integracion del
-- microservicio de Matricula Financiera.
--
-- Reproduce las tablas del esquema compartido que el modulo
-- consulta realmente (seccion 3.8.3 del documento), incluidas las
-- que no aparecen en src/main/resources/schema.sql pero si son
-- utilizadas por BdCompartidaRepository: matricula_financiera,
-- grupo y el subsistema de solicitudes.
-- =============================================================

DROP TABLE IF EXISTS solicitudes_en_concejo;
DROP TABLE IF EXISTS solicitud_beca_descuento;
DROP TABLE IF EXISTS solicitudes;
DROP TABLE IF EXISTS tipos_solicitudes;
DROP TABLE IF EXISTS matricula_financiera;
DROP TABLE IF EXISTS matriculas;
DROP TABLE IF EXISTS curso_docente;
DROP TABLE IF EXISTS cursos;
DROP TABLE IF EXISTS asignaturas;
DROP TABLE IF EXISTS docentes;
DROP TABLE IF EXISTS estudiantes;
DROP TABLE IF EXISTS grupo;
DROP TABLE IF EXISTS periodo_academico;
DROP TABLE IF EXISTS personas;

CREATE TABLE personas (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    identificacion      BIGINT       UNIQUE,
    nombre              VARCHAR(255),
    apellido            VARCHAR(255),
    correo_electronico  VARCHAR(255) UNIQUE,
    telefono            VARCHAR(255),
    genero              VARCHAR(255),
    tipo_identificacion VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE periodo_academico (
    id                  BIGINT      NOT NULL AUTO_INCREMENT,
    tag_periodo         INT         NOT NULL,
    fecha_inicio        DATE        NOT NULL,
    fecha_fin           DATE        NOT NULL,
    fecha_fin_matricula DATE        NOT NULL,
    descripcion         VARCHAR(255),
    estado              VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE grupo (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE estudiantes (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    id_persona           BIGINT       NULL DEFAULT NULL,
    codigo               VARCHAR(255) UNIQUE NULL DEFAULT NULL,
    cohorte              INT          NULL DEFAULT NULL,
    periodo_ingreso      VARCHAR(255) NULL DEFAULT NULL,
    semestre_financiero  INT          NULL DEFAULT NULL,
    semestre_academico   INT          NULL DEFAULT NULL,
    es_egresado_unicauca TINYINT(1)   NOT NULL DEFAULT 0,
    estado_maestria      VARCHAR(255) NULL DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_est_persona FOREIGN KEY (id_persona) REFERENCES personas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE docentes (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    id_persona   BIGINT       NULL DEFAULT NULL,
    codigo       VARCHAR(255),
    facultad     VARCHAR(255),
    departamento VARCHAR(255),
    estado       VARCHAR(50),
    PRIMARY KEY (id),
    CONSTRAINT fk_doc_persona FOREIGN KEY (id_persona) REFERENCES personas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE asignaturas (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    codigo_asignatura BIGINT       UNIQUE,
    nombre_asignatura VARCHAR(255) UNIQUE,
    estado_asignatura TINYINT,
    area_formacion    INT,
    tipo_asignatura   VARCHAR(255),
    creditos          INT,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cursos (
    id               INT          NOT NULL AUTO_INCREMENT,
    grupocurso       VARCHAR(20)  NOT NULL,
    periodo_id       BIGINT       NOT NULL,
    id_asignatura    BIGINT       NOT NULL,
    horariocurso     VARCHAR(100) NOT NULL,
    saloncurso       VARCHAR(50)  NOT NULL,
    observacioncurso VARCHAR(255) NULL,
    estado           TINYINT      NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT fk_cursos_periodo    FOREIGN KEY (periodo_id)    REFERENCES periodo_academico (id),
    CONSTRAINT fk_cursos_asignatura FOREIGN KEY (id_asignatura) REFERENCES asignaturas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE curso_docente (
    id_curso   INT    NOT NULL,
    id_docente BIGINT NOT NULL,
    PRIMARY KEY (id_curso, id_docente),
    CONSTRAINT fk_cd_curso   FOREIGN KEY (id_curso)   REFERENCES cursos (id),
    CONSTRAINT fk_cd_docente FOREIGN KEY (id_docente) REFERENCES docentes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE matriculas (
    id               INT NOT NULL AUTO_INCREMENT,
    id_estudiante    BIGINT,
    id_curso         INT,
    id_periodo       BIGINT,
    estado           INT,
    estado_matricula VARCHAR(50),
    observacion      VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_mat_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiantes (id),
    CONSTRAINT fk_mat_curso      FOREIGN KEY (id_curso)      REFERENCES cursos (id),
    CONSTRAINT fk_mat_periodo    FOREIGN KEY (id_periodo)    REFERENCES periodo_academico (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla propia del modulo. La restriccion de unicidad es la que permite que
-- la sentencia INSERT ... ON DUPLICATE KEY UPDATE sea idempotente.
CREATE TABLE matricula_financiera (
    id            BIGINT     NOT NULL AUTO_INCREMENT,
    estudiante_id BIGINT     NOT NULL,
    periodo_id    BIGINT     NOT NULL,
    grupo_id      BIGINT     NULL DEFAULT NULL,
    esta_pago     TINYINT(1) NULL DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_mf_estudiante_periodo UNIQUE (estudiante_id, periodo_id),
    CONSTRAINT fk_mf_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes (id),
    CONSTRAINT fk_mf_periodo    FOREIGN KEY (periodo_id)    REFERENCES periodo_academico (id),
    CONSTRAINT fk_mf_grupo      FOREIGN KEY (grupo_id)      REFERENCES grupo (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tipos_solicitudes (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(50)  NOT NULL,
    nombre VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE solicitudes (
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    id_estudiante      BIGINT      NOT NULL,
    id_tipo_solicitud  BIGINT      NOT NULL,
    estado             VARCHAR(50),
    fecha_solicitud    DATE,
    PRIMARY KEY (id),
    CONSTRAINT fk_sol_estudiante FOREIGN KEY (id_estudiante)     REFERENCES estudiantes (id),
    CONSTRAINT fk_sol_tipo       FOREIGN KEY (id_tipo_solicitud) REFERENCES tipos_solicitudes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE solicitud_beca_descuento (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    id_solicitud BIGINT      NOT NULL,
    tipo         VARCHAR(50),
    PRIMARY KEY (id),
    CONSTRAINT fk_sbd_solicitud FOREIGN KEY (id_solicitud) REFERENCES solicitudes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE solicitudes_en_concejo (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    id_solicitud    BIGINT       NOT NULL,
    porcentaje      FLOAT,
    resolucion      VARCHAR(255),
    avalado_concejo VARCHAR(50),
    fecha_inicio    DATE,
    fecha_fin       DATE,
    PRIMARY KEY (id),
    CONSTRAINT fk_sec_solicitud FOREIGN KEY (id_solicitud) REFERENCES solicitudes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
