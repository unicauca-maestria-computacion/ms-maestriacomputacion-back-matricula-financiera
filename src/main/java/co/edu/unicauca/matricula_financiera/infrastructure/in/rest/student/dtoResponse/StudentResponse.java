package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "StudentResponse",
        description = "Informacion de matricula financiera de un estudiante en un periodo academico")
public class StudentResponse {

    @Schema(description = "Codigo institucional del estudiante", example = "EST001")
    private String codigo;

    @Schema(description = "Nombres del estudiante", example = "Ana")
    private String nombre;

    @Schema(description = "Apellidos del estudiante", example = "Lopez")
    private String apellido;

    @Schema(description = "Numero de identificacion", example = "1061234567")
    private Long identificacion;

    @Schema(description = "Cohorte de ingreso", example = "2024")
    private Integer cohorte;

    @Schema(description = "Periodo de ingreso en formato anio-semestre", example = "2024-1")
    private String periodoIngreso;

    @Schema(description = "Semestre financiero, calculado desde el periodo de ingreso. "
                        + "Puede superar cuatro cuando el estudiante prolonga su permanencia",
            example = "5")
    private Integer semestreFinanciero;

    @Schema(description = "Semestre academico, acotado a cuatro por el plan de estudios",
            example = "4")
    private Integer semestreAcademico;

    @Schema(description = "Valor de matricula expresado en salarios minimos legales "
                        + "mensuales vigentes", example = "6")
    private Integer valorEnSMLV;

    @Schema(description = "Indica si el estudiante es egresado de la Universidad del Cauca",
            example = "false")
    private Boolean esEgresadoUnicauca;

    @Schema(description = "Indica si aplica el descuento por certificado de votacion",
            example = "true")
    private Boolean aplicaVotacion;

    @Schema(description = "Asignaturas matriculadas en el periodo, con su docente asignado")
    private List<MateriaResponse> materias;

    @Schema(description = "Becas y descuentos vigentes. Se mantiene por compatibilidad "
                        + "con el Front-End; su contenido es el mismo de becasDescuentos")
    private List<BecaDescuentoInfoResponse> becas;

    @Schema(description = "Becas y descuentos vigentes. Se mantiene por compatibilidad "
                        + "con el Front-End; su contenido es el mismo de becasDescuentos")
    private List<BecaDescuentoInfoResponse> descuentos;

    @Schema(description = "Becas y descuentos vigentes en las fechas del periodo consultado")
    private List<BecaDescuentoInfoResponse> becasDescuentos;

    @Schema(description = "Estado de pago de la matricula financiera del periodo",
            example = "true")
    private Boolean estaPago;

    @Schema(description = "Nombre del grupo de investigacion asociado", example = "GTI")
    private String grupoNombre;
}
