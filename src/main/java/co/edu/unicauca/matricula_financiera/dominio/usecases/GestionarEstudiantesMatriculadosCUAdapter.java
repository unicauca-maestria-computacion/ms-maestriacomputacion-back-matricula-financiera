package co.edu.unicauca.matricula_financiera.dominio.usecases;

import co.edu.unicauca.matricula_financiera.aplication.input.GestionarEstudiantesMatriculadosCUIntPort;
import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.aplication.output.FormateadorResultadosIntPort;
import co.edu.unicauca.matricula_financiera.aplication.output.GestionarComunicacionExternaGatewayIntPort;
import co.edu.unicauca.matricula_financiera.aplication.output.GestionarEstudiantesMatriculadosGatewayIntPort;

import java.util.List;

public class GestionarEstudiantesMatriculadosCUAdapter implements GestionarEstudiantesMatriculadosCUIntPort {
    
    private final GestionarEstudiantesMatriculadosGatewayIntPort objGestionarEstudiantesMatriculados;
    private final GestionarComunicacionExternaGatewayIntPort objGestionarComunicacionExternaGatewayIntPort;
    private final FormateadorResultadosIntPort objFormateadorResultados;
    
    public GestionarEstudiantesMatriculadosCUAdapter(
            GestionarEstudiantesMatriculadosGatewayIntPort objGestionarEstudiantesMatriculados,
            GestionarComunicacionExternaGatewayIntPort objGestionarComunicacionExternaGatewayIntPort,
            FormateadorResultadosIntPort objFormateadorResultados) {
        this.objGestionarEstudiantesMatriculados = objGestionarEstudiantesMatriculados;
        this.objGestionarComunicacionExternaGatewayIntPort = objGestionarComunicacionExternaGatewayIntPort;
        this.objFormateadorResultados = objFormateadorResultados;
    }
    
    @Override
    public List<Estudiante> obtenerEstudiantes(PeriodoAcademico periodo) {
        return objGestionarEstudiantesMatriculados.obtenerEstudiantes(periodo);
    }
    
    @Override
    public Estudiante obtenerEstudiante(Integer codigo) {
        return objGestionarEstudiantesMatriculados.obtenerEstudiante(codigo);
    }
    
    @Override
    public Boolean iniciarNuevaMatriculaFinanciera() {
        // TODO: Implementar la lógica de negocio para iniciar una nueva matrícula financiera
        return false;
    }
}

