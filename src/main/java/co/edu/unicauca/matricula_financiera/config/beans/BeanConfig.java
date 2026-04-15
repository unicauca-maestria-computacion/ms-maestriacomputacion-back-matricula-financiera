package co.edu.unicauca.matricula_financiera.config.beans;

import co.edu.unicauca.matricula_financiera.application.usecases.ManageEnrolledStudentsUseCaseImpl;
import co.edu.unicauca.matricula_financiera.domain.ports.in.ManageEnrolledStudentsUseCase;
import co.edu.unicauca.matricula_financiera.domain.ports.out.ResultFormatterPort;
import co.edu.unicauca.matricula_financiera.domain.ports.out.StudentGatewayPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    ManageEnrolledStudentsUseCase manageEnrolledStudentsUseCase(
            StudentGatewayPort gateway,
            ResultFormatterPort formatter) {
        return new ManageEnrolledStudentsUseCaseImpl(gateway, formatter);
    }
}
