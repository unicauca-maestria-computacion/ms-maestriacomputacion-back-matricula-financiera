package co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions;

public class EntidadNoExisteException extends RuntimeException {
    private String mensaje;
    private Object[] args;

    public EntidadNoExisteException(String mensaje, Object... args) {
        super(mensaje);
        this.mensaje = mensaje;
        this.args = args;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}

