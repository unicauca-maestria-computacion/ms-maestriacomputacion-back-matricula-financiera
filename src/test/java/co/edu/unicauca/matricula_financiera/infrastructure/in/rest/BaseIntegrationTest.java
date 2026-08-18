package co.edu.unicauca.matricula_financiera.infrastructure.in.rest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

/**
 * Clase base de las pruebas de integracion del microservicio de
 * Matricula Financiera.
 *
 * A diferencia de las pruebas unitarias, aqui no se sustituye ninguna
 * dependencia: se levanta el contexto completo de Spring Boot sobre un puerto
 * aleatorio y se ejecuta contra una instancia real de MySQL administrada por
 * Testcontainers. El esquema y los datos semilla se cargan desde
 * src/test/resources/db.
 *
 * Requiere un entorno con Docker disponible.
 *
 * Sobre la gestion del contenedor: se emplea el patron de contenedor unico
 * en lugar de las anotaciones @Testcontainers y @Container, que detienen el
 * contenedor al finalizar cada clase de prueba. Con una sola clase de
 * integracion ambas alternativas funcionan, pero al anadir una segunda la
 * segunda clase encontraria el contenedor detenido mientras Spring reutiliza
 * el contexto ya construido, cuya fuente de datos apunta al puerto anterior.
 * Arrancandolo una sola vez en el bloque de inicializacion estatica, el
 * contenedor permanece disponible durante toda la ejecucion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "dev", "test" })
public abstract class BaseIntegrationTest {

    @SuppressWarnings("resource")
    protected static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("maestria_computacion_test")
            .withUsername("test")
            .withPassword("test");

    static {
        MYSQL.start();
    }

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

    @BeforeEach
    void configureRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "";
    }
}
