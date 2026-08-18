package co.edu.unicauca.matricula_financiera.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuracion de la documentacion OpenAPI del microservicio de
 * Matricula Financiera.
 *
 * La especificacion se genera a partir del codigo mediante springdoc-openapi,
 * de modo que la documentacion no puede desincronizarse de los controladores
 * y de los objetos de transferencia realmente publicados. Sustituye al archivo
 * docs/OPENAPI.yml, que se mantenia de forma manual.
 *
 * Recursos publicados:
 *   - Especificacion JSON : /v3/api-docs
 *   - Interfaz Swagger UI : /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_SEGURIDAD = "bearerAuth";

    @Value("${server.port:8092}")
    private String puerto;

    @Bean
    public OpenAPI matriculaFinancieraOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Matricula Financiera")
                        .version("1.0.0")
                        .description("""
                                Microservicio del Sistema Academico-Administrativo de la Maestria en \
                                Computacion de la Universidad del Cauca.

                                Determina, para un periodo academico dado, que estudiantes se \
                                encuentran matriculados y cual es el valor de matricula que les \
                                corresponde, expresado en salarios minimos legales mensuales \
                                vigentes (SMLV). El modulo no liquida el valor en pesos: entrega el \
                                numero de salarios minimos junto con la informacion de beneficios \
                                asociada, y deja la conversion monetaria al microservicio de \
                                Informacion Presupuestaria.

                                Los codigos de error devueltos siguen el formato MF-XXXX y viajan \
                                en la propiedad errorCode del cuerpo de la respuesta, conforme al \
                                estandar RFC 7807 (Problem Details for HTTP APIs).""")
                        .contact(new Contact()
                                .name("Maestria en Computacion - Universidad del Cauca")
                                .email("maestriacomputacion@unicauca.edu.co"))
                        .license(new License()
                                .name("Uso academico - Universidad del Cauca")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + puerto)
                                .description("Entorno de desarrollo local")))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_SEGURIDAD, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        Token JWT emitido por la plataforma. Se valida con clave \
                                        simetrica mediante HMAC con SHA-512. Requerido unicamente \
                                        bajo el perfil prod.""")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_SEGURIDAD));
    }
}
