package co.edu.unicauca.matricula_financiera.infraestructura.input.configuration;

import co.edu.unicauca.matricula_financiera.aplication.input.GestionarEstudiantesMatriculadosCUIntPort;
import co.edu.unicauca.matricula_financiera.aplication.output.FormateadorResultadosIntPort;
import co.edu.unicauca.matricula_financiera.aplication.output.GestionarComunicacionExternaGatewayIntPort;
import co.edu.unicauca.matricula_financiera.aplication.output.GestionarEstudiantesMatriculadosGatewayIntPort;
import co.edu.unicauca.matricula_financiera.dominio.usecases.GestionarEstudiantesMatriculadosCUAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfigurations {
    
    @Bean
    public GestionarEstudiantesMatriculadosCUIntPort gestionarEstudiantesMatriculadosCUIntPort(
            GestionarEstudiantesMatriculadosGatewayIntPort objGestionarEstudiantesMatriculados,
            final GestionarComunicacionExternaGatewayIntPort objGestionarComunicacionExternaGatewayIntPort,
            FormateadorResultadosIntPort objFormateadorResultados) {
        return new GestionarEstudiantesMatriculadosCUAdapter(
                objGestionarEstudiantesMatriculados,
                objGestionarComunicacionExternaGatewayIntPort,
                objFormateadorResultados
        );
    }
}

