package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.controller;

import co.edu.unicauca.matricula_financiera.config.exceptions.custom.BusinessRuleViolatedException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.EntityNotFoundException;
import co.edu.unicauca.matricula_financiera.domain.enums.PeriodoEstado;
import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.domain.ports.in.ManageEnrolledStudentsUseCase;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoRequest.PeriodoAcademicoRequest;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.PeriodoAcademicoResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.StudentResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.mapper.StudentHttpMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa REST del microservicio de Matrícula Financiera.
 *
 * Se emplea la porción de contexto {@code @WebMvcTest}, que levanta únicamente
 * la infraestructura web: el controlador, el manejador global de excepciones,
 * los convertidores de mensajes y la validación declarativa. El caso de uso y
 * el mapeador se sustituyen por dobles de prueba, de modo que lo verificado
 * aquí es exclusivamente el contrato HTTP: rutas, códigos de estado,
 * serialización JSON y traducción de errores.
 */
@WebMvcTest(controllers = StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("StudentController - contrato de la API REST")
class StudentControllerTest {

    private static final String BASE = "/api/v1/gestion-matricula-financiera";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ManageEnrolledStudentsUseCase useCase;

    @MockitoBean
    private StudentHttpMapper mapper;

    private StudentResponse studentResponse(String codigo, Integer smlv) {
        StudentResponse r = new StudentResponse();
        r.setCodigo(codigo);
        r.setNombre("Ana");
        r.setApellido("Lopez");
        r.setSemestreFinanciero(3);
        r.setSemestreAcademico(3);
        r.setValorEnSMLV(smlv);
        r.setEstaPago(Boolean.TRUE);
        return r;
    }

    // ------------------------------------------------------------------
    // POST /estudiantes
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST /estudiantes retorna 200 y la lista de estudiantes del periodo")
    void getStudentsByPeriod_returnsOkWithStudentList() throws Exception {
        PeriodoAcademicoRequest request = new PeriodoAcademicoRequest(1, 2024);

        when(mapper.fromRequestToPeriodo(any())).thenReturn(new PeriodoAcademico());
        when(useCase.getStudentsByPeriod(any())).thenReturn(List.of(new Estudiante()));
        when(mapper.fromListToResponse(anyList()))
                .thenReturn(List.of(studentResponse("EST001", 6)));

        mockMvc.perform(post(BASE + "/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value("EST001"))
                .andExpect(jsonPath("$[0].valorEnSMLV").value(6))
                .andExpect(jsonPath("$[0].estaPago").value(true));

        verify(useCase).getStudentsByPeriod(any());
    }

    @Test
    @DisplayName("POST /estudiantes sin tagPeriodo retorna 400 con el detalle de validación")
    void getStudentsByPeriod_whenTagPeriodoIsMissing_returnsBadRequest() throws Exception {
        String body = "{\"anio\":2024}";

        mockMvc.perform(post(BASE + "/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MF-0006"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("POST /estudiantes sin anio retorna 400")
    void getStudentsByPeriod_whenAnioIsMissing_returnsBadRequest() throws Exception {
        String body = "{\"tagPeriodo\":1}";

        mockMvc.perform(post(BASE + "/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MF-0006"));
    }

    @Test
    @DisplayName("POST /estudiantes con periodo inexistente retorna 404 y codigo MF-0003")
    void getStudentsByPeriod_whenPeriodDoesNotExist_returnsNotFound() throws Exception {
        PeriodoAcademicoRequest request = new PeriodoAcademicoRequest(1, 1999);

        when(mapper.fromRequestToPeriodo(any())).thenReturn(new PeriodoAcademico());
        when(useCase.getStudentsByPeriod(any()))
                .thenThrow(new EntityNotFoundException("validation.period.notFound", 1, 1999));

        mockMvc.perform(post(BASE + "/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("MF-0003"))
                .andExpect(jsonPath("$.url").value(BASE + "/estudiantes"))
                .andExpect(jsonPath("$.method").value("POST"));
    }

    @Test
    @DisplayName("POST /estudiantes con violacion de regla de negocio retorna 422")
    void getStudentsByPeriod_whenBusinessRuleViolated_returnsUnprocessableEntity() throws Exception {
        PeriodoAcademicoRequest request = new PeriodoAcademicoRequest(1, 2024);

        when(mapper.fromRequestToPeriodo(any())).thenReturn(new PeriodoAcademico());
        when(useCase.getStudentsByPeriod(any()))
                .thenThrow(new BusinessRuleViolatedException("validation.period.notNull"));

        mockMvc.perform(post(BASE + "/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value("MF-0005"));
    }

    @Test
    @DisplayName("POST /estudiantes retorna una lista vacia cuando no hay matriculados")
    void getStudentsByPeriod_whenNoStudents_returnsEmptyArray() throws Exception {
        PeriodoAcademicoRequest request = new PeriodoAcademicoRequest(1, 2024);

        when(mapper.fromRequestToPeriodo(any())).thenReturn(new PeriodoAcademico());
        when(useCase.getStudentsByPeriod(any())).thenReturn(List.of());
        when(mapper.fromListToResponse(anyList())).thenReturn(List.of());

        mockMvc.perform(post(BASE + "/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ------------------------------------------------------------------
    // GET /estudiantes/{codigo}
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /estudiantes/{codigo} retorna 200 y el estudiante solicitado")
    void getStudentByCode_returnsOk() throws Exception {
        when(useCase.getStudentByCode(eq("EST001"), any(), any())).thenReturn(new Estudiante());
        when(mapper.fromEstudianteToResponse(any())).thenReturn(studentResponse("EST001", 6));

        mockMvc.perform(get(BASE + "/estudiantes/EST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("EST001"))
                .andExpect(jsonPath("$.valorEnSMLV").value(6));

        verify(useCase).getStudentByCode("EST001", null, null);
    }

    @Test
    @DisplayName("GET /estudiantes/{codigo} propaga tagPeriodo y anio cuando se envian")
    void getStudentByCode_forwardsOptionalQueryParams() throws Exception {
        when(useCase.getStudentByCode(eq("EST001"), eq(2), eq(2025))).thenReturn(new Estudiante());
        when(mapper.fromEstudianteToResponse(any())).thenReturn(studentResponse("EST001", 1));

        mockMvc.perform(get(BASE + "/estudiantes/EST001")
                        .param("tagPeriodo", "2")
                        .param("anio", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorEnSMLV").value(1));

        verify(useCase).getStudentByCode("EST001", 2, 2025);
    }

    @Test
    @DisplayName("GET /estudiantes/{codigo} con codigo inexistente retorna 404")
    void getStudentByCode_whenNotFound_returnsNotFound() throws Exception {
        when(useCase.getStudentByCode(eq("NOEXISTE"), any(), any()))
                .thenThrow(new EntityNotFoundException("validation.student.code.notFound", "NOEXISTE"));

        mockMvc.perform(get(BASE + "/estudiantes/NOEXISTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("MF-0003"));
    }

    @Test
    @DisplayName("GET /estudiantes/{codigo} con tagPeriodo no numerico retorna 500 controlado")
    void getStudentByCode_whenParamIsNotNumeric_isHandledByGlobalHandler() throws Exception {
        mockMvc.perform(get(BASE + "/estudiantes/EST001").param("tagPeriodo", "abc"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("MF-0001"));
    }

    // ------------------------------------------------------------------
    // GET /estudiantes/{codigo}/descuento-voto
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /descuento-voto retorna true cuando existe solicitud CER_VOTO aprobada")
    void tieneDescuentoVoto_returnsTrue() throws Exception {
        when(useCase.tieneDescuentoVoto("EST001")).thenReturn(true);

        mockMvc.perform(get(BASE + "/estudiantes/EST001/descuento-voto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("GET /descuento-voto retorna false cuando no existe la solicitud")
    void tieneDescuentoVoto_returnsFalse() throws Exception {
        when(useCase.tieneDescuentoVoto("EST002")).thenReturn(false);

        mockMvc.perform(get(BASE + "/estudiantes/EST002/descuento-voto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    // ------------------------------------------------------------------
    // GET /periodos y POST /iniciar
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /periodos retorna la lista de periodos academicos")
    void getAcademicPeriods_returnsOk() throws Exception {
        PeriodoAcademicoResponse periodo = new PeriodoAcademicoResponse(
                1L, 1, 2024,
                LocalDate.of(2024, 1, 15), LocalDate.of(2024, 6, 30), LocalDate.of(2024, 2, 15),
                "Periodo 2024-1", PeriodoEstado.ACTIVO);

        when(useCase.getAcademicPeriods()).thenReturn(List.of(new PeriodoAcademico()));
        when(mapper.fromListPeriodosToResponse(anyList())).thenReturn(List.of(periodo));

        mockMvc.perform(get(BASE + "/periodos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tagPeriodo").value(1))
                .andExpect(jsonPath("$[0].anio").value(2024))
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("POST /iniciar responde 200 con true")
    void iniciar_returnsOk() throws Exception {
        mockMvc.perform(post(BASE + "/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
