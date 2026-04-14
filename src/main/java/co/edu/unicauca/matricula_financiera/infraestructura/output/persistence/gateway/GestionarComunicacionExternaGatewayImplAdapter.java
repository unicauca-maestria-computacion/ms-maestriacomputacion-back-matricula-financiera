package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.gateway;

import co.edu.unicauca.matricula_financiera.aplication.output.GestionarComunicacionExternaGatewayIntPort;
import co.edu.unicauca.matricula_financiera.dominio.models.Docente;
import co.edu.unicauca.matricula_financiera.dominio.models.Materia;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto.AsignaturaExternaDto;
import co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto.CursoExternoDto;
import co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto.DocenteExternoDto;
import co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto.EstudianteExternoDto;
import co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto.MatriculaAcademicaExternaDto;
import co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto.PeriodoAcademicoExternoDto;
import co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto.PersonaExternaDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GestionarComunicacionExternaGatewayImplAdapter implements GestionarComunicacionExternaGatewayIntPort {

    private static final Logger log = LoggerFactory.getLogger(GestionarComunicacionExternaGatewayImplAdapter.class);

    private final RestTemplate restTemplate;
    private final String matriculaAcademicaBaseUrl;

    public GestionarComunicacionExternaGatewayImplAdapter(
            @Value("${matricula.academica.base-url:http://localhost:8081}") String matriculaAcademicaBaseUrl) {
        this.restTemplate = new RestTemplate();
        this.matriculaAcademicaBaseUrl = matriculaAcademicaBaseUrl;
    }

    // -------------------------------------------------------------------------
    // Verificar existencia del periodo en el microservicio académico
    // GET /api/periodos  →  busca por tagPeriodo + año
    // -------------------------------------------------------------------------
    @Override
    public boolean existePeriodoAcademicoExterno(PeriodoAcademico periodo) {
        if (periodo == null || periodo.getTagPeriodo() == null || periodo.getAño() == null) return false;
        try {
            boolean existe = obtenerPeriodosAcademicosExternos().stream().anyMatch(p -> {
                if (!periodo.getTagPeriodo().equals(p.getTagPeriodo())) return false;
                // comparar año: primero campo año, luego derivado de fechaInicio
                Integer añoExterno = p.getAño(); // usa getAño() que deriva de fechaInicio si año es null
                return añoExterno != null && añoExterno.equals(periodo.getAño());
            });
            log.info("existePeriodoAcademicoExterno tagPeriodo={} año={} → {}", periodo.getTagPeriodo(), periodo.getAño(), existe);
            return existe;
        } catch (ResourceAccessException e) {
            log.warn("Micro académico no disponible para verificar periodo: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error verificando periodo en micro académico: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<PeriodoAcademico> obtenerPeriodosAcademicosExternos() {
        try {
            String url = matriculaAcademicaBaseUrl + "/api/periodos";
            ResponseEntity<PeriodosApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<PeriodosApiResponse>() {});
            if (response.getBody() == null || response.getBody().data() == null) return new ArrayList<>();
            return response.getBody().data().stream()
                    .map(this::toPeriodoAcademico)
                    .toList();
        } catch (ResourceAccessException e) {
            log.warn("Micro académico no disponible para obtener periodos: {}", e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error obteniendo periodos desde micro académico: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> obtenerIdsEstudiantesPorPeriodoExterno(PeriodoAcademico periodo) {
        // Ya no se usa — los estudiantes se obtienen directamente desde la BD compartida
        return new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Métodos existentes
    // -------------------------------------------------------------------------

    @Override
    public Optional<EstudianteExternoDto> obtenerDatosEstudianteExterno(Long estudianteId) {
        if (estudianteId == null) return Optional.empty();
        try {
            String url = matriculaAcademicaBaseUrl + "/api/estudiante-docente/estudiante/" + estudianteId;
            EstudianteApiResponse response = restTemplate.getForObject(url, EstudianteApiResponse.class);
            if (response == null || response.data() == null) return Optional.empty();
            return Optional.of(response.data());
        } catch (ResourceAccessException e) {
            log.warn("Micro académico no disponible para datos estudiante {}: {}", estudianteId, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error consultando datos estudiante {}: {}", estudianteId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<MatriculaAcademica> obtenerMatriculasAcademicas(PeriodoAcademico periodo) {
        return new ArrayList<>();
    }

    @Override
    public List<MatriculaAcademica> obtenerMatriculasAcademicasPorEstudiante(Long estudianteId, Integer periodo, Integer anio) {
        if (estudianteId == null) return new ArrayList<>();
        try {
            String url = matriculaAcademicaBaseUrl + "/api/matricula/estudiante/" + estudianteId;
            ApiResponseWrapper response = restTemplate.getForObject(url, ApiResponseWrapper.class);
            if (response == null || response.data() == null) return new ArrayList<>();

            List<MatriculaAcademica> resultado = new ArrayList<>();
            for (MatriculaAcademicaExternaDto dto : response.data()) {
                PeriodoAcademicoExternoDto periodoDto = (dto.getCurso() != null) ? dto.getCurso().getPeriodo() : null;
                if (!matchesPeriodo(periodoDto, periodo, anio)) continue;
                resultado.add(toMatriculaAcademica(dto));
            }
            return resultado;
        } catch (ResourceAccessException e) {
            log.warn("Micro académico no disponible para estudianteId {}: {}", estudianteId, e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error consultando matrículas académicas para estudianteId {}: {}", estudianteId, e.getMessage());
            return new ArrayList<>();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private PeriodoAcademico toPeriodoAcademico(PeriodoAcademicoExternoDto dto) {
        PeriodoAcademico p = new PeriodoAcademico();
        p.setId(dto.getId());
        p.setTagPeriodo(dto.getTagPeriodo());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setFechaFinMatricula(dto.getFechaFinMatricula());
        p.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) {
            try {
                p.setEstado(co.edu.unicauca.matricula_financiera.dominio.models.enums.PeriodoEstado.valueOf(dto.getEstado().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        return p;
    }

    private boolean matchesPeriodo(PeriodoAcademicoExternoDto p, Integer periodo, Integer anio) {
        if (p == null || p.getTagPeriodo() == null || p.getFechaInicio() == null) return false;
        return p.getTagPeriodo().equals(periodo) && p.getFechaInicio().getYear() == anio;
    }

    private MatriculaAcademica toMatriculaAcademica(MatriculaAcademicaExternaDto dto) {
        MatriculaAcademica ma = new MatriculaAcademica();
        CursoExternoDto curso = dto.getCurso();
        PeriodoAcademicoExternoDto p = (curso != null) ? curso.getPeriodo() : null;

        if (p != null) {
            ma.setSemestre(p.getTagPeriodo());
            PeriodoAcademico pf = new PeriodoAcademico();
            pf.setTagPeriodo(p.getTagPeriodo());
            pf.setFechaInicio(p.getFechaInicio());
            pf.setFechaFin(p.getFechaFin());
            pf.setFechaFinMatricula(p.getFechaFinMatricula());
            ma.setObjPeriodoAcademico(pf);
        }

        if (curso != null) {
            Materia materia = new Materia();
            AsignaturaExternaDto asignatura = curso.getAsignatura();
            if (asignatura != null) {
                materia.setCodigo_oid(asignatura.getCodigo());
                materia.setMateria(asignatura.getNombre());
                materia.setCreditos(asignatura.getCreditos());
                materia.setTipo(asignatura.getTipo());
            }
            materia.setGrupoClase(curso.getGrupo());
            materia.setHorario(curso.getHorario());
            materia.setSalon(curso.getSalon());
            materia.setObservacion(dto.getObservacion());
            materia.setEstadoMatricula(dto.getEstado());
            materia.setSemestreFinanciero(p != null ? p.getTagPeriodo() : null);

            if (curso.getDocentes() != null && !curso.getDocentes().isEmpty()) {
                DocenteExternoDto docenteDto = curso.getDocentes().get(0);
                Docente docente = new Docente();
                PersonaExternaDto persona = docenteDto.getPersona();
                if (persona != null) {
                    docente.setNombre(persona.getNombre());
                    docente.setApellido(persona.getApellido());
                }
                materia.setObjDocente(docente);
            }
            ma.setMaterias(new ArrayList<>(List.of(materia)));
        } else {
            ma.setMaterias(new ArrayList<>());
        }
        return ma;
    }

    // -------------------------------------------------------------------------
    // DTOs internos para deserializar respuestas del micro académico
    // -------------------------------------------------------------------------

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiResponseWrapper(
            @JsonProperty("data") List<MatriculaAcademicaExternaDto> data) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record EstudianteApiResponse(
            @JsonProperty("data") EstudianteExternoDto data) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PeriodosApiResponse(
            @JsonProperty("data") List<PeriodoAcademicoExternoDto> data) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MatriculasAgrupadasApiResponse(
            @JsonProperty("data") List<MatriculaAgrupadaExternaDto> data) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MatriculaAgrupadaExternaDto(
            @JsonProperty("idCurso") Long idCurso,
            @JsonProperty("asignatura") String asignatura,
            @JsonProperty("grupo") String grupo,
            @JsonProperty("estado") String estado,
            @JsonProperty("cantidadEstudiante") Long cantidadEstudiante) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MatriculasCursoApiResponse(
            @JsonProperty("data") List<MatriculaCursoExternaDto> data) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MatriculaCursoExternaDto(
            @JsonProperty("id") Long id,
            @JsonProperty("estudiante") EstudianteIdDto estudiante,
            @JsonProperty("estado") String estado) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record EstudianteIdDto(
            @JsonProperty("id") Long id) {}
}
