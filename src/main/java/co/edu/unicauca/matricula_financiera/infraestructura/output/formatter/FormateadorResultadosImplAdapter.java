package co.edu.unicauca.matricula_financiera.infraestructura.output.formatter;

import co.edu.unicauca.matricula_financiera.aplication.output.FormateadorResultadosIntPort;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions.EntidadNoExisteException;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions.EntidadYaExisteException;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions.ReglaNegocioException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FormateadorResultadosImplAdapter implements FormateadorResultadosIntPort {

    private static final Logger logger = LoggerFactory.getLogger(FormateadorResultadosImplAdapter.class);

    @Override
    public void errorEntidadYaExiste(String mensaje, Object... args) {
        logger.error(mensaje, args);
        EntidadYaExisteException objException = new EntidadYaExisteException(mensaje, args);
        throw objException;
    }

    @Override
    public void errorEntidadNoExiste(String mensaje, Object... args) {
        logger.error(mensaje, args);
        EntidadNoExisteException objException = new EntidadNoExisteException(mensaje, args);
        throw objException;
    }

    @Override
    public void errorReglaNegocioViolada(String mensaje, Object... args) {
        logger.error(mensaje, args);
        ReglaNegocioException objException = new ReglaNegocioException(mensaje, args);
        throw objException;
    }
}
