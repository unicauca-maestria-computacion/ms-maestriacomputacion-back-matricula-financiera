package co.edu.unicauca.matricula_financiera.infraestructura.output.external;

import co.edu.unicauca.matricula_financiera.aplication.output.HojaVidaClientOutputPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementación stub del cliente de Hoja de Vida.
 * Retorna valores por defecto hasta que el microservicio de Hoja de Vida esté disponible.
 * TODO: reemplazar por implementación HTTP cuando el microservicio esté listo.
 */
@Component
public class HojaVidaClientStubAdapter implements HojaVidaClientOutputPort {

    @Override
    public boolean esEgresado(String codigoEstudiante) {
        // TODO: consultar GET /api/hoja-vida/estudiante/{codigo}/egresado
        return false;
    }

    @Override
    public boolean tieneCertificadoVotacion(String codigoEstudiante) {
        // TODO: consultar GET /api/hoja-vida/estudiante/{codigo}/votacion
        return false;
    }

    @Override
    public List<Float> obtenerDescuentosYBecas(String codigoEstudiante) {
        // TODO: consultar GET /api/hoja-vida/estudiante/{codigo}/descuentos
        return List.of();
    }
}
