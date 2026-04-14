package co.edu.unicauca.matricula_financiera.aplication.output;

import java.util.List;

/**
 * Puerto de salida hacia el microservicio de Hoja de Vida.
 * Provee información de becas y descuentos del estudiante.
 */
public interface HojaVidaClientOutputPort {

    /**
     * Indica si el estudiante es egresado de la Universidad del Cauca.
     * Aplica descuento del 5%.
     */
    boolean esEgresado(String codigoEstudiante);

    /**
     * Indica si el estudiante tiene certificado de votación vigente.
     * Aplica descuento del 10%.
     */
    boolean tieneCertificadoVotacion(String codigoEstudiante);

    /**
     * Retorna los porcentajes de descuento/beca activos del estudiante.
     * Cada elemento es un porcentaje (ej: 25.0 para 25%).
     */
    List<Float> obtenerDescuentosYBecas(String codigoEstudiante);
}
