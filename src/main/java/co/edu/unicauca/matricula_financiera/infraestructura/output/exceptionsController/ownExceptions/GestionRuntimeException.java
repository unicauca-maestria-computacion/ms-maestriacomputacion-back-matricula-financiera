package co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions;

import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.exceptionsStructure.CodigoError;

public class GestionRuntimeException extends RuntimeException {
    private CodigoError codigoError;
    private static final String FORMATO_EXCEPCION = "Error: %s";
    private String reglaNegocio;
    private Object[] args;

    public GestionRuntimeException(CodigoError codigoError, String reglaNegocio, Object... args) {
        super(reglaNegocio);
        this.codigoError = codigoError;
        this.reglaNegocio = reglaNegocio;
        this.args = args;
    }

    public CodigoError getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(CodigoError codigoError) {
        this.codigoError = codigoError;
    }

    public String getReglaNegocio() {
        return reglaNegocio;
    }

    public void setReglaNegocio(String reglaNegocio) {
        this.reglaNegocio = reglaNegocio;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String formatException() {
        return String.format(FORMATO_EXCEPCION, reglaNegocio);
    }
}

