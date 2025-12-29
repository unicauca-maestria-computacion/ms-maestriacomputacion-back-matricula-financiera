package co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions;

import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.exceptionsStructure.CodigoError;

public class GestionRuntimeException extends RuntimeException {
    private CodigoError codigoError;
    private static final String FORMATO_EXCEPCION = "Error: %s";
    private String reglaNegocio;
    
    public GestionRuntimeException(CodigoError codigoError, String reglaNegocio) {
        super(reglaNegocio);
        this.codigoError = codigoError;
        this.reglaNegocio = reglaNegocio;
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
    
    public String formatException() {
        return String.format(FORMATO_EXCEPCION, reglaNegocio);
    }
}

