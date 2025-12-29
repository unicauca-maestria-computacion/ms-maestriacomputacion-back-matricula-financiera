package co.edu.unicauca.matricula_financiera.aplication.output;

import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;

import java.util.List;

public interface GestionarComunicacionExternaGatewayIntPort {
    List<MatriculaAcademica> obtenerMatriculasAcademicas(PeriodoAcademico periodo);
}

