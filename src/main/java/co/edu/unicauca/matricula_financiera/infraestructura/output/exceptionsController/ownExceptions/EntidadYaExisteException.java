package co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions;

public class EntidadYaExisteException extends RuntimeException {
    private String llaveMensaje;
    private String codigo;
    
    public EntidadYaExisteException(String llaveMensaje, String codigo) {
        super(llaveMensaje);
        this.llaveMensaje = llaveMensaje;
        this.codigo = codigo;
    }
    
    public String getLlaveMensaje() {
        return llaveMensaje;
    }
    
    public void setLlaveMensaje(String llaveMensaje) {
        this.llaveMensaje = llaveMensaje;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}

