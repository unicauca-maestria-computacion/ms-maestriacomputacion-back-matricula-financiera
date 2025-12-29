package co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.exceptionsStructure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {
    private String codigoError;
    private String mensaje;
    private Integer codigoHttp;
    private String url;
    private String metodo;

    public Error() {
    }

    @JsonCreator
    public Error(
            @JsonProperty("codigoError") String codigoError,
            @JsonProperty("mensaje") String mensaje,
            @JsonProperty("codigoHttp") Integer codigoHttp,
            @JsonProperty("url") String url,
            @JsonProperty("metodo") String metodo) {
        this.codigoError = codigoError;
        this.mensaje = mensaje;
        this.codigoHttp = codigoHttp;
        this.url = url;
        this.metodo = metodo;
    }

    public String getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(String codigoError) {
        this.codigoError = codigoError;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getCodigoHttp() {
        return codigoHttp;
    }

    public void setCodigoHttp(Integer codigoHttp) {
        this.codigoHttp = codigoHttp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
}

