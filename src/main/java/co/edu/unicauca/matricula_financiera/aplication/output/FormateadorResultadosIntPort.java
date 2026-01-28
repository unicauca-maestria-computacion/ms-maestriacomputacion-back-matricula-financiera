package co.edu.unicauca.matricula_financiera.aplication.output;

public interface FormateadorResultadosIntPort {
    void errorEntidadYaExiste(String mensaje, Object... args);
    void errorEntidadNoExiste(String mensaje, Object... args);
    void errorReglaNegocioViolada(String mensaje, Object... args);
}

