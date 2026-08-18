package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student;

import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.BaseIntegrationTest;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Pruebas de integracion de la API de Matricula Financiera.
 *
 * Recorren el camino completo controlador -> caso de uso -> adaptador de
 * persistencia -> MySQL, sin dobles de prueba. Su proposito es verificar que
 * las reglas financieras producen el mismo resultado cuando operan sobre datos
 * reales y que las consultas SQL escritas a mano son correctas, aspectos que
 * las pruebas unitarias con Mockito no pueden cubrir.
 */
@DisplayName("API de Matricula Financiera - pruebas de integracion")
class EstudiantesApiIT extends BaseIntegrationTest {

    private static final String BASE = "/api/v1/gestion-matricula-financiera";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ------------------------------------------------------------------
    // POST /estudiantes
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Retorna los tres estudiantes matriculados en el periodo 2024-1")
    void postEstudiantes_returnsEnrolledStudents() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("tagPeriodo", 1, "anio", 2024))
        .when()
                .post(BASE + "/estudiantes")
        .then()
                .statusCode(200)
                .body("size()", equalTo(3))
                .body("codigo", hasItem("EST001"))
                .body("codigo", hasItem("EST002"))
                .body("codigo", hasItem("EST003"));
    }

    @Test
    @DisplayName("Aplica la Tabla 3.7: 6 SMLV en primer semestre, 1 SMLV desde el noveno")
    void postEstudiantes_appliesTuitionRules() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("tagPeriodo", 1, "anio", 2024))
        .when()
                .post(BASE + "/estudiantes")
        .then()
                .statusCode(200)
                // EST001 ingreso 2024-1 -> semestre financiero 1 -> 6 SMLV
                .body("find { it.codigo == 'EST001' }.semestreFinanciero", equalTo(1))
                .body("find { it.codigo == 'EST001' }.valorEnSMLV", equalTo(6))
                // EST002 ingreso 2020-1 -> semestre financiero 9 -> 1 SMLV (Acuerdo 044 de 2012)
                .body("find { it.codigo == 'EST002' }.semestreFinanciero", equalTo(9))
                .body("find { it.codigo == 'EST002' }.valorEnSMLV", equalTo(1))
                // EST003 semestre 5 cursando unicamente Trabajo de Grado 2 -> 1 SMLV
                .body("find { it.codigo == 'EST003' }.semestreFinanciero", equalTo(5))
                .body("find { it.codigo == 'EST003' }.valorEnSMLV", equalTo(1));
    }

    @Test
    @DisplayName("El semestre academico se acota a cuatro aunque el financiero siga creciendo")
    void postEstudiantes_capsAcademicSemesterAtFour() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("tagPeriodo", 1, "anio", 2024))
        .when()
                .post(BASE + "/estudiantes")
        .then()
                .statusCode(200)
                .body("find { it.codigo == 'EST002' }.semestreAcademico", equalTo(4))
                .body("find { it.codigo == 'EST002' }.semestreFinanciero", equalTo(9));
    }

    @Test
    @DisplayName("Incluye datos personales, asignaturas y docente resueltos desde el esquema compartido")
    void postEstudiantes_includesEnrichedData() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("tagPeriodo", 1, "anio", 2024))
        .when()
                .post(BASE + "/estudiantes")
        .then()
                .statusCode(200)
                .body("find { it.codigo == 'EST001' }.nombre", equalTo("Ana"))
                .body("find { it.codigo == 'EST001' }.apellido", equalTo("Lopez"))
                .body("find { it.codigo == 'EST001' }.identificacion", equalTo(1061234567))
                .body("find { it.codigo == 'EST001' }.materias.size()", equalTo(1))
                .body("find { it.codigo == 'EST001' }.materias[0].materia", equalTo("Algoritmos Avanzados"))
                .body("find { it.codigo == 'EST001' }.materias[0].docente.nombre", equalTo("Pedro"));
    }

    @Test
    @DisplayName("Resuelve las becas vigentes en las fechas del periodo consultado")
    void postEstudiantes_resolvesActiveScholarships() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("tagPeriodo", 1, "anio", 2024))
        .when()
                .post(BASE + "/estudiantes")
        .then()
                .statusCode(200)
                .body("find { it.codigo == 'EST001' }.becasDescuentos.size()", equalTo(1))
                .body("find { it.codigo == 'EST001' }.becasDescuentos[0].tipo", equalTo("BECA"))
                .body("find { it.codigo == 'EST001' }.becasDescuentos[0].resolucion", equalTo("RES-2024-001"))
                .body("find { it.codigo == 'EST003' }.becasDescuentos.size()", equalTo(0));
    }

    @Test
    @DisplayName("Refleja el descuento por certificado de votacion aprobado")
    void postEstudiantes_reflectsVotingDiscount() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("tagPeriodo", 1, "anio", 2024))
        .when()
                .post(BASE + "/estudiantes")
        .then()
                .statusCode(200)
                .body("find { it.codigo == 'EST001' }.aplicaVotacion", equalTo(true))
                // EST002 tiene la solicitud en estado RECHAZADA
                .body("find { it.codigo == 'EST002' }.aplicaVotacion", equalTo(false));
    }

    /**
     * Verifica el auto-registro descrito en la seccion 3.8.3: la consulta crea
     * el registro financiero del periodo cuando no existe y hereda el ultimo
     * grupo de investigacion conocido del estudiante.
     */
    @Test
    @DisplayName("Crea el registro financiero del periodo heredando el ultimo grupo conocido")
    void postEstudiantes_createsFinancialEnrollmentInheritingGroup() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("tagPeriodo", 1, "anio", 2024))
        .when()
                .post(BASE + "/estudiantes")
        .then()
                .statusCode(200);

        Integer registros = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM matricula_financiera WHERE periodo_id = 1", Integer.class);
        assertThat(registros).isEqualTo(3);

        Long grupoHeredado = jdbcTemplate.queryForObject(
                "SELECT grupo_id FROM matricula_financiera WHERE estudiante_id = 2 AND periodo_id = 1",
                Long.class);
        assertThat(grupoHeredado).isEqualTo(2L);
    }

    /**
     * La operacion debe poder repetirse sin duplicar registros ni alterar el
     * estado de pago ya establecido.
     */
    @Test
    @DisplayName("Repetir la consulta es idempotente sobre la tabla matricula_financiera")
    void postEstudiantes_isIdempotent() {
        for (int i = 0; i < 3; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("tagPeriodo", 1, "anio", 2024))
            .when()
                    .post(BASE + "/estudiantes")
            .then()
                    .statusCode(200);
        }

        Integer registros = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM matricula_financiera WHERE periodo_id = 1", Integer.class);
        assertThat(registros).isEqualTo(3);
    }

    // ------------------------------------------------------------------
    // Validacion y manejo de errores
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Un periodo inexistente produce 404 con codigo MF-0003")
    void postEstudiantes_whenPeriodDoesNotExist_returns404() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("tagPeriodo", 1, "anio", 1999))
        .when()
                .post(BASE + "/estudiantes")
        .then()
                .statusCode(404)
                .body("errorCode", equalTo("MF-0003"))
                .body("detail", notNullValue());
    }

    @Test
    @DisplayName("Una peticion sin anio produce 400 con codigo MF-0006")
    void postEstudiantes_whenPayloadIsIncomplete_returns400() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("tagPeriodo", 1))
        .when()
                .post(BASE + "/estudiantes")
        .then()
                .statusCode(400)
                .body("errorCode", equalTo("MF-0006"))
                .body("validationErrors", notNullValue());
    }

    // ------------------------------------------------------------------
    // GET /estudiantes/{codigo}
    // ------------------------------------------------------------------

    @Test
    @DisplayName("La consulta individual resuelve el periodo activo cuando no se envian parametros")
    void getEstudiante_resolvesActivePeriodByDefault() {
        given()
        .when()
                .get(BASE + "/estudiantes/EST001")
        .then()
                .statusCode(200)
                .body("codigo", equalTo("EST001"))
                .body("semestreFinanciero", equalTo(1))
                .body("valorEnSMLV", equalTo(6));
    }

    @Test
    @DisplayName("La consulta individual admite tagPeriodo y anio explicitos")
    void getEstudiante_acceptsExplicitPeriod() {
        given()
                .queryParam("tagPeriodo", 1)
                .queryParam("anio", 2024)
        .when()
                .get(BASE + "/estudiantes/EST002")
        .then()
                .statusCode(200)
                .body("codigo", equalTo("EST002"))
                .body("valorEnSMLV", equalTo(1));
    }

    @Test
    @DisplayName("Un codigo inexistente produce 404")
    void getEstudiante_whenNotFound_returns404() {
        given()
        .when()
                .get(BASE + "/estudiantes/NOEXISTE")
        .then()
                .statusCode(404)
                .body("errorCode", equalTo("MF-0003"));
    }

    @Test
    @DisplayName("El indicador de descuento por votacion se expone de forma independiente")
    void getDescuentoVoto_returnsBoolean() {
        String conDescuento = given()
                .when().get(BASE + "/estudiantes/EST001/descuento-voto")
                .then().statusCode(200)
                .extract().asString().trim();

        String sinDescuento = given()
                .when().get(BASE + "/estudiantes/EST002/descuento-voto")
                .then().statusCode(200)
                .extract().asString().trim();

        assertThat(conDescuento).isEqualTo("true");
        assertThat(sinDescuento).isEqualTo("false");
    }

    // ------------------------------------------------------------------
    // GET /periodos
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Lista los periodos academicos ordenados por fecha de inicio descendente")
    void getPeriodos_returnsAllPeriods() {
        given()
        .when()
                .get(BASE + "/periodos")
        .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].tagPeriodo", equalTo(1))
                .body("[0].anio", equalTo(2024))
                .body("[0].estado", equalTo("ACTIVO"))
                .body("[1].estado", equalTo("CERRADO"));
    }
}
