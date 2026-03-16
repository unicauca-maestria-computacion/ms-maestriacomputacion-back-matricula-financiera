package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.Beca;
import co.edu.unicauca.matricula_financiera.dominio.models.Descuento;
import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.Materia;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaFinanciera;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.BecaDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.DescuentoDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.DocenteDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.EstudianteDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.MateriaDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.MatriculaAcademicaDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.MatriculaFinancieraDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.PeriodoAcademicoDTORespuesta;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstudianteMapperInfraestructuraImpl implements EstudianteMapperInfraestructura {

    @Override
    public EstudianteDTORespuesta mappearDeEstudianteARespuesta(Estudiante estudiante) {
        if (estudiante == null) {
            return null;
        }
        return new EstudianteDTORespuesta(
                estudiante.getCodigo(),
                estudiante.getCohorte(),
                estudiante.getPeriodoIngreso(),
                estudiante.getSemestreFinanciero(),
                mappearMatriculasFinancieras(estudiante.getMatriculasFinancieras()),
                mappearDescuentos(estudiante.getDescuentos()),
                mappearBecas(estudiante.getBecas()),
                mappearMatriculasAcademicas(estudiante.getMatriculasAcademicas())
        );
    }

    @Override
    public List<EstudianteDTORespuesta> mappearDeListaEstudianteARespuesta(List<Estudiante> estudiantes) {
        if (estudiantes == null) {
            return Collections.emptyList();
        }
        return estudiantes.stream()
                .map(this::mappearDeEstudianteARespuesta)
                .collect(Collectors.toList());
    }

    private List<MatriculaFinancieraDTORespuesta> mappearMatriculasFinancieras(List<MatriculaFinanciera> matriculas) {
        if (matriculas == null) return Collections.emptyList();
        return matriculas.stream().map(m -> new MatriculaFinancieraDTORespuesta(
                m.getFechaMatricula(),
                m.getValorMatricula(),
                m.getPagada(),
                mappearPeriodo(m.getObjPeriodoAcademico())
        )).collect(Collectors.toList());
    }

    private List<DescuentoDTORespuesta> mappearDescuentos(List<Descuento> descuentos) {
        if (descuentos == null) return Collections.emptyList();
        return descuentos.stream().map(d -> new DescuentoDTORespuesta(
                d.getTipoDescuento(),
                d.getPorcentaje()
        )).collect(Collectors.toList());
    }

    private List<BecaDTORespuesta> mappearBecas(List<Beca> becas) {
        if (becas == null) return Collections.emptyList();
        return becas.stream().map(b -> new BecaDTORespuesta(
                b.getResolucion(),
                b.getPorcentaje()
        )).collect(Collectors.toList());
    }

    private List<MatriculaAcademicaDTORespuesta> mappearMatriculasAcademicas(List<MatriculaAcademica> matriculas) {
        if (matriculas == null) return Collections.emptyList();
        return matriculas.stream().map(m -> new MatriculaAcademicaDTORespuesta(
                m.getSemestre(),
                mappearMaterias(m.getMaterias()),
                mappearPeriodo(m.getObjPeriodoAcademico())
        )).collect(Collectors.toList());
    }

    private List<MateriaDTORespuesta> mappearMaterias(List<Materia> materias) {
        if (materias == null) return Collections.emptyList();
        return materias.stream().map(m -> new MateriaDTORespuesta(
                m.getCodigo_oid(),
                m.getSemestreAcademico(),
                m.getMateria(),
                m.getObjDocente() != null ? new DocenteDTORespuesta(
                        m.getObjDocente().getNombre(),
                        m.getObjDocente().getApellido()
                ) : null,
                m.getGrupoClase()
        )).collect(Collectors.toList());
    }

    private PeriodoAcademicoDTORespuesta mappearPeriodo(PeriodoAcademico periodo) {
        if (periodo == null) return null;
        return new PeriodoAcademicoDTORespuesta(periodo.getPeriodo(), periodo.getAño());
    }
}
