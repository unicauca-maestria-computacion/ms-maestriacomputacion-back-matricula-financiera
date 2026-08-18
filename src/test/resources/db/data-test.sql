-- =============================================================
-- Datos semilla para las pruebas de integracion.
--
-- El escenario se construye de modo que cada estudiante ejercite una
-- rama distinta de la regla de calculo del valor de matricula
-- (Tabla 3.7 del documento):
--
--   EST001 -> ingreso 2024-1, consulta 2024-1 -> semestre 1  -> 6 SMLV
--   EST002 -> ingreso 2020-1, consulta 2024-1 -> semestre 9  -> 1 SMLV (Acuerdo 044 de 2012)
--   EST003 -> ingreso 2022-1, consulta 2024-1 -> semestre 5, solo Trabajo de Grado 2 -> 1 SMLV
-- =============================================================

INSERT INTO personas (id, identificacion, nombre, apellido, correo_electronico) VALUES
    (1, 1061234567, 'Ana',    'Lopez',    'ana.lopez@unicauca.edu.co'),
    (2, 1062345678, 'Carlos', 'Ramirez',  'carlos.ramirez@unicauca.edu.co'),
    (3, 1063456789, 'Lucia',  'Gomez',    'lucia.gomez@unicauca.edu.co'),
    (9, 1069999999, 'Pedro',  'Martinez', 'pedro.martinez@unicauca.edu.co');

INSERT INTO periodo_academico (id, tag_periodo, fecha_inicio, fecha_fin, fecha_fin_matricula, descripcion, estado) VALUES
    (1, 1, '2024-01-15', '2024-06-30', '2024-02-15', 'Periodo 2024-1', 'ACTIVO'),
    (2, 2, '2023-08-01', '2023-12-15', '2023-08-20', 'Periodo 2023-2', 'CERRADO');

INSERT INTO grupo (id, nombre) VALUES
    (1, 'GTI'),
    (2, 'IDIS');

INSERT INTO estudiantes (id, id_persona, codigo, cohorte, periodo_ingreso, semestre_financiero, semestre_academico, es_egresado_unicauca) VALUES
    (1, 1, 'EST001', 2024, '2024-1', 1, 1, 0),
    (2, 2, 'EST002', 2020, '2020-1', 9, 4, 1),
    (3, 3, 'EST003', 2022, '2022-1', 5, 4, 0);

INSERT INTO docentes (id, id_persona, codigo, facultad, departamento, estado) VALUES
    (1, 9, 'DOC001', 'FIET', 'Sistemas', 'ACTIVO');

INSERT INTO asignaturas (id, codigo_asignatura, nombre_asignatura, estado_asignatura, tipo_asignatura, creditos) VALUES
    (1, 1001, 'Algoritmos Avanzados', 1, 'OBLIGATORIA', 4),
    (2, 1002, 'Trabajo de Grado 2',   1, 'OBLIGATORIA', 6);

INSERT INTO cursos (id, grupocurso, periodo_id, id_asignatura, horariocurso, saloncurso, estado) VALUES
    (1, 'A', 1, 1, 'Lunes 8:00-10:00',   'Salon 201', 1),
    (2, 'A', 1, 2, 'Martes 14:00-16:00', 'Salon 305', 1);

INSERT INTO curso_docente (id_curso, id_docente) VALUES
    (1, 1),
    (2, 1);

-- EST001 y EST002 cursan Algoritmos Avanzados; EST003 cursa unicamente Trabajo de Grado 2.
INSERT INTO matriculas (id, id_estudiante, id_curso, id_periodo, estado, estado_matricula) VALUES
    (1, 1, 1, 1, 1, 'MATRICULADO'),
    (2, 2, 1, 1, 1, 'MATRICULADO'),
    (3, 3, 2, 1, 1, 'MATRICULADO');

-- Grupo de investigacion previamente conocido de EST002, que el modulo debe heredar
-- al crear su registro financiero del periodo 2024-1.
INSERT INTO matricula_financiera (id, estudiante_id, periodo_id, grupo_id, esta_pago) VALUES
    (1, 2, 2, 2, 1);

-- Solicitud de certificado de votacion aprobada para EST001.
INSERT INTO tipos_solicitudes (id, codigo, nombre) VALUES
    (1, 'CER_VOTO', 'Certificado de votacion'),
    (2, 'BECA',     'Solicitud de beca');

INSERT INTO solicitudes (id, id_estudiante, id_tipo_solicitud, estado, fecha_solicitud) VALUES
    (1, 1, 1, 'APROBADA', '2024-01-20'),
    (2, 2, 1, 'RECHAZADA', '2024-01-20'),
    (3, 1, 2, 'APROBADA', '2024-01-25');

INSERT INTO solicitud_beca_descuento (id, id_solicitud, tipo) VALUES
    (1, 3, 'BECA');

INSERT INTO solicitudes_en_concejo (id, id_solicitud, porcentaje, resolucion, avalado_concejo, fecha_inicio, fecha_fin) VALUES
    (1, 3, 50.0, 'RES-2024-001', 'SI', '2024-01-01', '2024-12-31');
