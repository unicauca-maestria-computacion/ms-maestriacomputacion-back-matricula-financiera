package co.edu.unicauca.matricula_financiera.config.security;

import co.edu.unicauca.matricula_financiera.domain.ports.in.ManageEnrolledStudentsUseCase;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.controller.StudentController;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.mapper.StudentHttpMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica las reglas de autorizacion declaradas para el perfil de produccion.
 *
 * Antes de la incorporacion de estas reglas, la cadena de filtros exigia
 * unicamente que la peticion estuviera autenticada, de modo que un usuario con
 * perfil de estudiante podia invocar cualquier operacion de gestion. Las
 * pruebas de esta clase fijan ese comportamiento: comprueban tanto que el
 * perfil autorizado accede como que el no autorizado recibe 403, condicion sin
 * la cual una regla puede parecer activa sin estarlo.
 *
 * El token se construye mediante las utilidades de prueba de Spring Security,
 * lo que permite verificar la cadena de filtros sin depender del emisor real.
 */
@WebMvcTest(controllers = StudentController.class)
@Import(SecurityConfig.ProdSecurityConfig.class)
@ActiveProfiles("prod")
@TestPropertySource(properties = {
        "app.jwt-secret=clave-de-prueba-de-al-menos-sesenta-y-cuatro-bytes-para-el-algoritmo-hs512"
})
@DisplayName("SecurityConfig - reglas de autorizacion por rol (perfil prod)")
class SecurityRulesTest {

    private static final String BASE = "/api/v1/gestion-matricula-financiera";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ManageEnrolledStudentsUseCase useCase;

    @MockitoBean
    private StudentHttpMapper mapper;

    /** Token con el claim que emite el modulo de autenticacion. */
    private static org.springframework.test.web.servlet.request.RequestPostProcessor comoRol(String rol) {
        return jwt().jwt(builder -> builder.claim("rol", rol))
                .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority(rol));
    }

    @Nested
    @DisplayName("Sin credenciales")
    class SinCredenciales {

        @Test
        @DisplayName("una peticion sin token recibe 401")
        void sinTokenRecibe401() throws Exception {
            mockMvc.perform(get(BASE + "/periodos"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("los puntos de verificacion de estado permanecen accesibles")
        void actuatorEsPublico() throws Exception {
            // El endpoint del actuador no forma parte de esta porcion de
            // contexto, de modo que el codigo concreto que devuelve depende del
            // tratamiento que el manejador global de excepciones da a la
            // ausencia de manejador. Lo que aqui interesa verificar es que la
            // cadena de filtros no interviene: si exigiera credenciales, la
            // respuesta seria 401, y si exigiera un rol, 403.
            mockMvc.perform(get("/actuator/health"))
                    .andExpect(result -> assertThat(result.getResponse().getStatus())
                            .as("la cadena de filtros no debe exigir credenciales en el actuador")
                            .isNotIn(401, 403));
        }
    }

    @Nested
    @DisplayName("Operaciones de gestion, reservadas al coordinador")
    class OperacionesDeGestion {

        @Test
        @DisplayName("el coordinador puede consultar el listado de matriculados")
        void coordinadorAccedeAlListado() throws Exception {
            mockMvc.perform(post(BASE + "/estudiantes")
                            .with(comoRol("ROLE_COORDINADOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"tagPeriodo\":1,\"anio\":2024}"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("el estudiante no puede consultar el listado completo de matriculados")
        void estudianteNoAccedeAlListado() throws Exception {
            mockMvc.perform(post(BASE + "/estudiantes")
                            .with(comoRol("ROLE_ESTUDIANTE"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"tagPeriodo\":1,\"anio\":2024}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("el estudiante no puede iniciar el proceso de matricula")
        void estudianteNoInicia() throws Exception {
            mockMvc.perform(post(BASE + "/iniciar").with(comoRol("ROLE_ESTUDIANTE")))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("el coordinador puede iniciar el proceso de matricula")
        void coordinadorInicia() throws Exception {
            mockMvc.perform(post(BASE + "/iniciar").with(comoRol("ROLE_COORDINADOR")))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Consultas accesibles a ambos perfiles")
    class ConsultasCompartidas {

        @Test
        @DisplayName("el estudiante puede consultar el catalogo de periodos")
        void estudianteConsultaPeriodos() throws Exception {
            mockMvc.perform(get(BASE + "/periodos").with(comoRol("ROLE_ESTUDIANTE")))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("el estudiante puede consultar el detalle de un estudiante")
        void estudianteConsultaDetalle() throws Exception {
            mockMvc.perform(get(BASE + "/estudiantes/EST001").with(comoRol("ROLE_ESTUDIANTE")))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("el estudiante puede consultar el descuento por votacion")
        void estudianteConsultaDescuento() throws Exception {
            mockMvc.perform(get(BASE + "/estudiantes/EST001/descuento-voto")
                            .with(comoRol("ROLE_ESTUDIANTE")))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Perfiles ajenos al modulo")
    class PerfilesAjenos {

        @Test
        @DisplayName("un perfil no contemplado recibe 403 aunque el token sea valido")
        void perfilAjenoRecibe403() throws Exception {
            mockMvc.perform(get(BASE + "/periodos").with(comoRol("ROLE_DOCENTE")))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("un token valido sin ningun rol recibe 403")
        void tokenSinRolRecibe403() throws Exception {
            mockMvc.perform(get(BASE + "/periodos").with(jwt()))
                    .andExpect(status().isForbidden());
        }
    }
}
