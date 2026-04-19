package co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.repository;

import co.edu.unicauca.matricula_financiera.domain.enums.PeriodoEstado;
import co.edu.unicauca.matricula_financiera.domain.models.Docente;
import co.edu.unicauca.matricula_financiera.domain.models.Materia;
import co.edu.unicauca.matricula_financiera.domain.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BdCompartidaRepository {

    private final JdbcTemplate jdbc;

    public BdCompartidaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PeriodoAcademico> findAllPeriodos() {
        String sql = """
                SELECT id, tag_periodo, YEAR(fecha_inicio) AS anio,
                       fecha_inicio, fecha_fin, fecha_fin_matricula, descripcion, estado
                FROM periodo_academico
                ORDER BY fecha_inicio DESC
                """;
        return jdbc.query(sql, (rs, i) -> mapPeriodo(rs));
    }

    public PeriodoAcademico findPeriodoByTagAndAnio(Integer tagPeriodo, Integer anio) {
        String sql = """
                SELECT id, tag_periodo, YEAR(fecha_inicio) AS anio,
                       fecha_inicio, fecha_fin, fecha_fin_matricula, descripcion, estado
                FROM periodo_academico
                WHERE tag_periodo = ? AND YEAR(fecha_inicio) = ?
                LIMIT 1
                """;
        List<PeriodoAcademico> result = jdbc.query(sql, (rs, i) -> mapPeriodo(rs), tagPeriodo, anio);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<MatriculaAcademica> findMatriculasPorEstudianteYPeriodo(Long estudianteId,
                                                                         Integer tagPeriodo,
                                                                         Integer anio) {
        String sql = """
                SELECT m.id AS mat_id, m.estado_matricula, m.observacion,
                       c.grupocurso, c.horariocurso, c.saloncurso,
                       a.codigo_asignatura, a.nombre_asignatura, a.creditos, a.tipo_asignatura,
                       p.id AS periodo_id, p.tag_periodo, YEAR(p.fecha_inicio) AS anio,
                       p.fecha_inicio, p.fecha_fin, p.fecha_fin_matricula, p.descripcion, p.estado,
                       per.nombre AS docente_nombre, per.apellido AS docente_apellido
                FROM matriculas m
                JOIN cursos c            ON m.id_curso = c.id
                JOIN asignaturas a       ON c.id_asignatura = a.id
                JOIN periodo_academico p ON m.id_periodo = p.id
                LEFT JOIN curso_docente cd ON cd.id_curso = c.id
                LEFT JOIN docentes d       ON d.id = cd.id_docente
                LEFT JOIN personas per     ON per.id = d.id_persona
                WHERE m.id_estudiante = ?
                  AND p.tag_periodo = ?
                  AND YEAR(p.fecha_inicio) = ?
                """;
        return jdbc.query(sql, (rs, i) -> {
            MatriculaAcademica ma = new MatriculaAcademica();

            PeriodoAcademico periodo = new PeriodoAcademico();
            periodo.setId(rs.getLong("periodo_id"));
            periodo.setTagPeriodo(rs.getInt("tag_periodo"));
            periodo.setFechaInicio(rs.getObject("fecha_inicio", LocalDate.class));
            periodo.setFechaFin(rs.getObject("fecha_fin", LocalDate.class));
            periodo.setFechaFinMatricula(rs.getObject("fecha_fin_matricula", LocalDate.class));
            periodo.setDescripcion(rs.getString("descripcion"));
            ma.setObjPeriodoAcademico(periodo);
            ma.setSemestre(rs.getInt("tag_periodo"));

            Docente docente = new Docente();
            docente.setNombre(rs.getString("docente_nombre"));
            docente.setApellido(rs.getString("docente_apellido"));

            Materia materia = new Materia();
            materia.setCodigo_oid(String.valueOf(rs.getLong("codigo_asignatura")));
            materia.setMateria(rs.getString("nombre_asignatura"));
            materia.setCreditos(rs.getInt("creditos"));
            materia.setTipo(rs.getString("tipo_asignatura"));
            materia.setGrupoClase(rs.getString("grupocurso"));
            materia.setHorario(rs.getString("horariocurso"));
            materia.setSalon(rs.getString("saloncurso"));
            materia.setEstadoMatricula(rs.getString("estado_matricula"));
            materia.setObservacion(rs.getString("observacion"));
            materia.setSemestreFinanciero(rs.getInt("tag_periodo"));
            materia.setObjDocente(docente);

            ma.setMaterias(new ArrayList<>(List.of(materia)));
            return ma;
        }, estudianteId, tagPeriodo, anio);
    }

    public Object[] findDatosPersonalesEstudiante(Long estudianteId) {
        String sql = """
                SELECT p.nombre, p.apellido, p.identificacion,
                       e.semestre_academico
                FROM estudiantes e
                LEFT JOIN personas p ON p.id = e.id_persona
                WHERE e.id = ?
                """;
        List<Object[]> result = jdbc.query(sql,
                (rs, i) -> new Object[]{
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getLong("identificacion"),
                        rs.getObject("semestre_academico")
                }, estudianteId);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean tieneSolicitudCerVotoAprobada(String codigoEstudiante) {
        String sql = """
                SELECT COUNT(*) FROM solicitudes s
                INNER JOIN tipos_solicitudes ts ON ts.id = s.id_tipo_solicitud
                INNER JOIN estudiantes e ON e.id = s.id_estudiante
                WHERE e.codigo = ?
                  AND ts.codigo = 'CER_VOTO'
                  AND UPPER(s.estado) = 'APROBADA'
                """;
        Integer count = jdbc.queryForObject(sql, Integer.class, codigoEstudiante);
        return count != null && count > 0;
    }

    private PeriodoAcademico mapPeriodo(java.sql.ResultSet rs) throws java.sql.SQLException {
        PeriodoAcademico p = new PeriodoAcademico();
        p.setId(rs.getLong("id"));
        p.setTagPeriodo(rs.getInt("tag_periodo"));
        p.setFechaInicio(rs.getObject("fecha_inicio", LocalDate.class));
        p.setFechaFin(rs.getObject("fecha_fin", LocalDate.class));
        p.setFechaFinMatricula(rs.getObject("fecha_fin_matricula", LocalDate.class));
        p.setDescripcion(rs.getString("descripcion"));
        String estado = rs.getString("estado");
        if (estado != null) {
            try { p.setEstado(PeriodoEstado.valueOf(estado.toUpperCase())); }
            catch (IllegalArgumentException ignored) {}
        }
        return p;
    }
}
