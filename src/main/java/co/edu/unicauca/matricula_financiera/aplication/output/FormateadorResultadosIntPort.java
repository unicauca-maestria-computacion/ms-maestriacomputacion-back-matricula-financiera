package co.edu.unicauca.matricula_financiera.aplication.output;

public interface FormateadorResultadosIntPort {
    void errorEntidadYaExiste(String mensaje);
    void errorEntidadNoExiste(String mensaje);
    void errorReglaNegocioViolada(String mensaje);
}

