package co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.exceptionsStructure;

public class ErrorUtils {
    
    public static Error crearError(final String codigoError,
                                   final String llaveMensaje,
                                   final Integer codigoHttp,
                                   final String url,
                                   final String metodo) {
        Error errorCustom = new Error();
        errorCustom.setCodigoError(codigoError);
        errorCustom.setMensaje(llaveMensaje);
        errorCustom.setCodigoHttp(codigoHttp);
        errorCustom.setUrl(url);
        errorCustom.setMetodo(metodo);
        return errorCustom;
    }
}

