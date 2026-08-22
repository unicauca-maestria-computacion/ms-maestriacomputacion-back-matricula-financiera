package co.edu.unicauca.matricula_financiera.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Ruta base de los servicios expuestos por el microservicio. */
    static final String BASE = "/api/v1/gestion-matricula-financiera";

    /**
     * Nombres de rol sin el prefijo {@code ROLE_}, que Spring Security antepone
     * al evaluar {@code hasRole}. Corresponden a los registrados en la tabla
     * {@code roles} del esquema compartido.
     */
    static final String COORDINADOR = "COORDINADOR";
    static final String ESTUDIANTE = "ESTUDIANTE";

    @Configuration
    @Profile("dev")
    static class DevSecurityConfig {
        @Bean
        SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }

    @Configuration
    @Profile("prod")
    static class ProdSecurityConfig {

        @Value("${app.jwt-secret}")
        private String jwtSecret;

        /**
         * Claims en los que se busca el perfil del usuario. Se declara como
         * propiedad para que un cambio en el contrato del token emitido por el
         * modulo de autenticacion no obligue a recompilar el servicio.
         */
        @Value("${app.security.roles-claims:rol,roles,authorities}")
        private List<String> rolesClaims;

        @Bean
        JwtDecoder jwtDecoder() {
            SecretKeySpec key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA512");
            return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(
                    org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512).build();
        }

        /**
         * Sustituye al conversor por omision, que solo interpreta el claim
         * {@code scope}. Sin esta sustitucion la peticion llegaria autenticada
         * pero sin autoridades, y toda regla basada en el rol denegaria el acceso.
         */
        @Bean
        JwtAuthenticationConverter jwtAuthenticationConverter() {
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(new JwtRoleConverter(rolesClaims));
            return converter;
        }

        /**
         * Reglas de autorizacion por ruta.
         *
         * <p>El criterio aplicado distingue dos perfiles. El coordinador es el
         * unico autorizado a obtener el listado completo de estudiantes
         * matriculados en un periodo y a iniciar el proceso de matricula, por
         * tratarse de operaciones de gestion sobre el conjunto del programa. El
         * estudiante accede a la consulta individual y al catalogo de periodos,
         * que son las operaciones que sustentan su vista de matricula.</p>
         *
         * <p>Queda pendiente la verificacion de pertenencia sobre la consulta
         * individual: la regla declarada aqui autoriza al perfil, no comprueba
         * que el codigo consultado sea el del propio solicitante. Esa
         * comprobacion exige conocer la correspondencia entre el sujeto del
         * token y el codigo del estudiante, y se documenta como limitacion.</p>
         */
        @Bean
        SecurityFilterChain prodSecurityFilterChain(
                HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter)
                throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                            // Documentacion OpenAPI: la especificacion describe el contrato
                            // publico de la API, no expone datos financieros ni personales.
                            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                            .permitAll()

                            // Consulta de un estudiante concreto y de su descuento por
                            // certificado de votacion: coordinador y estudiante.
                            .requestMatchers(HttpMethod.GET, BASE + "/estudiantes/*/descuento-voto")
                            .hasAnyRole(COORDINADOR, ESTUDIANTE)
                            .requestMatchers(HttpMethod.GET, BASE + "/estudiantes/*")
                            .hasAnyRole(COORDINADOR, ESTUDIANTE)

                            // Catalogo de periodos academicos: requerido por ambas vistas.
                            .requestMatchers(HttpMethod.GET, BASE + "/periodos")
                            .hasAnyRole(COORDINADOR, ESTUDIANTE)

                            // Listado completo de matriculados e inicio del proceso:
                            // operaciones de gestion reservadas al coordinador.
                            .requestMatchers(HttpMethod.POST, BASE + "/estudiantes")
                            .hasRole(COORDINADOR)
                            .requestMatchers(HttpMethod.POST, BASE + "/iniciar")
                            .hasRole(COORDINADOR)

                            // Cualquier ruta no contemplada arriba queda reservada al
                            // coordinador: se prefiere denegar por omision antes que
                            // exponer una operacion nueva sin regla explicita.
                            .anyRequest().hasRole(COORDINADOR))
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                            jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                    .build();
        }
    }
}
