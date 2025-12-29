package co.edu.unicauca.matricula_financiera.dominio.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoAcademico {
    private Integer periodo;
    private Integer año;
    private List<MatriculaFinanciera> matriculasFinancieras;
    // Nota: Estas clases no están definidas en el diagrama, se pueden agregar después si es necesario
    // private ConfiguracionReportesFinanciero objConfiguracionReporteFinanciero;
    // private ConfiguracionReportesGrupos objConfiguracionReporteGrupo;
    // private ProyeccionEstudiante objProyeccionEstudiante;
}

