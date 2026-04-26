package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.mapper;

import co.edu.unicauca.matricula_financiera.domain.models.BecaDescuentoInfo;
import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.BecaDescuentoInfoResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.StudentResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StudentHttpMapperTest {

    private final StudentHttpMapper mapper = Mappers.getMapper(StudentHttpMapper.class);

    @Test
    void fromBecaDescuentoInfoToResponse_shouldMapAllFieldsCorrectly() {
        // Arrange
        BecaDescuentoInfo info = new BecaDescuentoInfo("DESCUENTO", 25.0f, "RES-2024-001", "avalada", "SI");

        // Act
        BecaDescuentoInfoResponse response = mapper.fromBecaDescuentoInfoToResponse(info);

        // Assert
        assertThat(response.getTipo()).isEqualTo("DESCUENTO");
        assertThat(response.getPorcentaje()).isEqualTo(25.0f);
        assertThat(response.getResolucion()).isEqualTo("RES-2024-001");
        assertThat(response.getEstado()).isEqualTo("avalada");
        assertThat(response.getAvaladoConcejo()).isEqualTo("SI");
    }

    @Test
    void fromEstudianteToResponse_shouldMapBecasDescuentosCorrectly() {
        // Arrange
        BecaDescuentoInfo beca = new BecaDescuentoInfo("BECA", 50.0f, "RES-001", "avalada", "SI");
        Estudiante estudiante = new Estudiante();
        estudiante.setBecasDescuentos(List.of(beca));

        // Act
        StudentResponse response = mapper.fromEstudianteToResponse(estudiante);

        // Assert
        assertThat(response.getBecasDescuentos()).hasSize(1);
        assertThat(response.getBecasDescuentos().get(0).getTipo()).isEqualTo("BECA");
        assertThat(response.getBecasDescuentos().get(0).getEstado()).isEqualTo("avalada");
    }

    @Test
    void fromEstudianteToResponse_whenBecasDescuentosIsNull_shouldReturnEmptyList() {
        // Arrange
        Estudiante estudiante = new Estudiante();
        estudiante.setBecasDescuentos(null);

        // Act
        StudentResponse response = mapper.fromEstudianteToResponse(estudiante);

        // Assert
        assertThat(response.getBecasDescuentos()).isEmpty();
    }
}
