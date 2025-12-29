package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.gateway;

import co.edu.unicauca.matricula_financiera.aplication.output.GestionarComunicacionExternaGatewayIntPort;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GestionarComunicacionExternaGatewayImplAdapter implements GestionarComunicacionExternaGatewayIntPort {
    
    @Override
    public List<MatriculaAcademica> obtenerMatriculasAcademicas(PeriodoAcademico periodo) {
        // TODO: Implementar la lógica para obtener matrículas académicas desde un servicio externo
        // Por ahora retorna una lista vacía
        return List.of();
    }
}

