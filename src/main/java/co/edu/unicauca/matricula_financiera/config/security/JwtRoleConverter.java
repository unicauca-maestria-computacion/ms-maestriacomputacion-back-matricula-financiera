package co.edu.unicauca.matricula_financiera.config.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Traduce el perfil declarado en el token web JSON a autoridades de Spring
 * Security.
 *
 * <p>Motivo de su existencia. El conversor que Spring Security aplica cuando no
 * se le indica otro lee unicamente el claim {@code scope} y antepone el prefijo
 * {@code SCOPE_}. El modulo de autenticacion del sistema academico-administrativo
 * no emplea ese claim: declara el perfil del usuario en un claim propio. En
 * ausencia de esta clase, la coleccion de autoridades asociada a la peticion se
 * construye vacia y cualquier regla basada en {@code hasRole} denegaria el
 * acceso a la totalidad de los usuarios, incluido el coordinador.</p>
 *
 * <p>Criterio de diseno. El nombre del claim no se fija en el codigo sino que se
 * recibe por configuracion, y se admite mas de un candidato. La razon es que el
 * contrato del token pertenece a un componente preexistente, ajeno al alcance de
 * este trabajo: si dicho componente modificara el nombre del claim, la
 * adaptacion se resuelve en el archivo de configuracion y no exige recompilar el
 * servicio. Por la misma razon se acepta tanto un valor unico como una lista, y
 * tanto la forma {@code ROLE_COORDINADOR} como {@code COORDINADOR}.</p>
 */
public class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /** Claims consultados cuando la configuracion no indica otros. */
    public static final List<String> CLAIMS_POR_OMISION = List.of("rol", "roles", "authorities");

    private static final String PREFIJO_ROL = "ROLE_";

    /**
     * Claves admitidas cuando el claim transporta objetos en lugar de cadenas,
     * forma habitual cuando el emisor serializa directamente la entidad del rol.
     */
    private static final Set<String> CLAVES_ANIDADAS =
            Set.of("authority", "rol", "role", "nombre_rol", "nombreRol", "nombre", "name");

    private final List<String> claims;

    public JwtRoleConverter() {
        this(CLAIMS_POR_OMISION);
    }

    public JwtRoleConverter(List<String> claims) {
        this.claims = (claims == null || claims.isEmpty())
                ? CLAIMS_POR_OMISION
                : List.copyOf(claims);
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<GrantedAuthority> autoridades = new LinkedHashSet<>();
        for (String claim : claims) {
            extraer(jwt.getClaims().get(claim), autoridades);
        }
        return autoridades;
    }

    /**
     * Recorre el valor del claim con independencia de su forma. Se admiten una
     * cadena simple, una cadena con varios roles separados por comas o espacios,
     * una coleccion de cadenas y una coleccion de objetos.
     */
    private void extraer(Object valor, Set<GrantedAuthority> destino) {
        if (valor == null) {
            return;
        }
        if (valor instanceof String cadena) {
            for (String fragmento : cadena.split("[\\s,]+")) {
                anadir(fragmento, destino);
            }
            return;
        }
        if (valor instanceof Collection<?> coleccion) {
            for (Object elemento : coleccion) {
                extraer(elemento, destino);
            }
            return;
        }
        if (valor instanceof Map<?, ?> mapa) {
            for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
                if (entrada.getKey() instanceof String clave
                        && CLAVES_ANIDADAS.contains(clave)) {
                    extraer(entrada.getValue(), destino);
                }
            }
        }
    }

    /**
     * Normaliza un rol a la forma que Spring Security espera. El metodo
     * {@code hasRole} antepone {@code ROLE_} al nombre indicado, de modo que la
     * autoridad almacenada debe incluir ese prefijo exactamente una vez.
     */
    private void anadir(String rol, Set<GrantedAuthority> destino) {
        if (rol == null) {
            return;
        }
        String normalizado = rol.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        if (normalizado.isEmpty()) {
            return;
        }
        if (!normalizado.startsWith(PREFIJO_ROL)) {
            normalizado = PREFIJO_ROL + normalizado;
        }
        destino.add(new SimpleGrantedAuthority(normalizado));
    }
}
