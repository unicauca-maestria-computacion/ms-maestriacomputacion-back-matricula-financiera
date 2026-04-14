package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.Beca;
import co.edu.unicauca.matricula_financiera.dominio.models.Descuento;
import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.Materia;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.BecaDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.DescuentoDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.DocenteDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.EstudianteDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.MateriaDTORespuesta;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstudianteMapperInfraestructuraImpl implements EstudianteMapperInfraestructura {

    @Override
    public EstudianteDTORespuesta mappearDeEstudianteARespuesta(Estudiante estudiante) {
        if (estudiante == null) return null;

        List<MateriaDTORespuesta> materias = new ArrayList<>();
        if (estudiante.getMatriculasAcademicas() != null) {
            for (MatriculaAcademica ma : estudiante.getMatriculasAcademicas()) {
                materias.addAll(mappearMaterias(ma.getMaterias()));
            }
        }

        return new EstudianteDTORespuesta(
                estudiante.getCodigo(),
                estudiante.getNombre(),
                estudiante.getApellido(),
                estudiante.getIdentificacion(),
                estudiante.getCohorte(),
                estudiante.getPeriodoIngreso(),
                estudiante.getSemestreFinanciero(),
                estudiante.getSemestreAcademico(),
                estudiante.getValorEnSMLV(),
                mappearDescuentos(estudiante.getDescuentos()),
                mappearBecas(estudiante.getBecas()),
                materias);
    }

    @Override
    public List<EstudianteDTORespuesta> mappearDeListaEstudianteARespuesta(List<Estudiante> estudiantes) {
        if (estudiantes == null) return new ArrayList<>();
        return estudiantes.stream()
                .map(this::mappearDeEstudianteARespuesta)
                .collect(Collectors.toList());
    }

    private List<DescuentoDTORespuesta> mappearDescuentos(List<Descuento> descuentos) {
        if (descuentos == null) return new ArrayList<>();
        return descuentos.stream()
                .map(d -> new DescuentoDTORespuesta(d.getTipoDescuento(), d.getPorcentaje()))
                .collect(Collectors.toList());
    }

    private List<BecaDTORespuesta> mappearBecas(List<Beca> becas) {
        if (becas == null) return new ArrayList<>();
        return becas.stream()
                .map(b -> new BecaDTORespuesta(b.getDedicacion(), b.getPorcentaje()))
                .collect(Collectors.toList());
    }

    private List<MateriaDTORespuesta> mappearMaterias(List<Materia> materias) {
        if (materias == null) return new ArrayList<>();
        return materias.stream().map(m -> new MateriaDTORespuesta(
                m.getCodigo_oid(),
                m.getMateria(),
                m.getObjDocente() != null
                        ? new DocenteDTORespuesta(m.getObjDocente().getNombre(), m.getObjDocente().getApellido())
                        : null,
                m.getGrupoClase()
        )).collect(Collectors.toList());
    }
}
