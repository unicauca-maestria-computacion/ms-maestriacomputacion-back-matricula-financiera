package co.edu.unicauca.matricula_financiera.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica la traduccion del perfil declarado en el token a autoridades de
 * Spring Security.
 *
 * Estas pruebas cubren el punto exacto que impedia aplicar una regla por rol:
 * el conversor por omision de Spring Security solo interpreta el claim
 * {@code scope}, de modo que el perfil emitido por el modulo de autenticacion
 * quedaba fuera del objeto de autenticacion.
 */
@DisplayName("JwtRoleConverter - traduccion del perfil del token a autoridades")
class JwtRoleConverterTest {

    private final JwtRoleConverter converter = new JwtRoleConverter();

    private Jwt tokenCon(String claim, Object valor) {
        return Jwt.withTokenValue("token")
                .header("alg", "HS512")
                .subject("usuario@unicauca.edu.co")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim(claim, valor)
                .build();
    }

    private List<String> nombres(Collection<GrantedAuthority> autoridades) {
        return autoridades.stream().map(GrantedAuthority::getAuthority).toList();
    }

    @Test
    @DisplayName("lee el claim 'rol' cuando transporta una unica cadena")
    void leeClaimRolComoCadena() {
        var resultado = converter.convert(tokenCon("rol", "ROLE_COORDINADOR"));
        assertThat(nombres(resultado)).containsExactly("ROLE_COORDINADOR");
    }

    @Test
    @DisplayName("antepone el prefijo ROLE_ cuando el token no lo incluye")
    void anteponePrefijo() {
        var resultado = converter.convert(tokenCon("rol", "coordinador"));
        assertThat(nombres(resultado)).containsExactly("ROLE_COORDINADOR");
    }

    @Test
    @DisplayName("no duplica el prefijo cuando el token ya lo trae")
    void noDuplicaPrefijo() {
        var resultado = converter.convert(tokenCon("rol", "ROLE_ESTUDIANTE"));
        assertThat(nombres(resultado)).containsExactly("ROLE_ESTUDIANTE");
    }

    @Test
    @DisplayName("acepta una lista de roles")
    void aceptaLista() {
        var resultado = converter.convert(
                tokenCon("roles", List.of("ROLE_COORDINADOR", "ROLE_DOCENTE")));
        assertThat(nombres(resultado))
                .containsExactlyInAnyOrder("ROLE_COORDINADOR", "ROLE_DOCENTE");
    }

    @Test
    @DisplayName("acepta varios roles en una cadena separada por comas o espacios")
    void aceptaCadenaSeparada() {
        var resultado = converter.convert(tokenCon("rol", "COORDINADOR, DOCENTE"));
        assertThat(nombres(resultado))
                .containsExactlyInAnyOrder("ROLE_COORDINADOR", "ROLE_DOCENTE");
    }

    @Test
    @DisplayName("acepta objetos anidados, forma en que algunos emisores serializan el rol")
    void aceptaObjetosAnidados() {
        var resultado = converter.convert(
                tokenCon("roles", List.of(Map.of("nombre_rol", "ROLE_COORDINADOR"))));
        assertThat(nombres(resultado)).containsExactly("ROLE_COORDINADOR");
    }

    @Test
    @DisplayName("devuelve una coleccion vacia cuando el token no declara ningun rol")
    void sinRolDevuelveVacio() {
        var resultado = converter.convert(tokenCon("otro", "valor"));
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("no produce autoridades espurias a partir de cadenas vacias")
    void ignoraCadenasVacias() {
        var resultado = converter.convert(tokenCon("rol", "  ,  "));
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("consulta unicamente los claims configurados")
    void respetaClaimsConfigurados() {
        var especifico = new JwtRoleConverter(List.of("perfil"));
        assertThat(especifico.convert(tokenCon("rol", "COORDINADOR"))).isEmpty();
        assertThat(nombres(especifico.convert(tokenCon("perfil", "COORDINADOR"))))
                .containsExactly("ROLE_COORDINADOR");
    }
}
