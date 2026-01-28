package co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions;

import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.exceptionsStructure.CodigoError;

public class ReglaNegocioException extends GestionRuntimeException {

    public ReglaNegocioException(String reglaNegocio, Object... args) {
        super(CodigoError.VIOLACION_REGLA_DE_NEGOCIO, reglaNegocio, args);
    }
}

