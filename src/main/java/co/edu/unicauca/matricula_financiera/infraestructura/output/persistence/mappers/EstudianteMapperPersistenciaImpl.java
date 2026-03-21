package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.Beca;
import co.edu.unicauca.matricula_financiera.dominio.models.Descuento;
import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaFinanciera;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.BecaEntity;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.DescuentoEntity;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.EstudianteEntity;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.MatriculaFinancieraEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstudianteMapperPersistenciaImpl implements EstudianteMapperPersistencia {

    @Override
    public Estudiante mappearDeEntityAEstudiante(EstudianteEntity estudiante) {
        if (estudiante == null) {
            return null;
        }
        Estudiante domain = new Estudiante();
        domain.setCodigo(estudiante.getCodigo());
        domain.setNombre(estudiante.getNombre());
        domain.setApellido(estudiante.getApellido());
        domain.setIdentificacion(estudiante.getIdentificacion());
        domain.setCohorte(estudiante.getCohorte());
        domain.setPeriodoIngreso(estudiante.getPeriodoIngreso());
        domain.setSemestreFinanciero(estudiante.getSemestreFinanciero());

        // Mapear matrículas financieras
        if (estudiante.getMatriculasFinancieras() != null) {
            domain.setMatriculasFinancieras(estudiante.getMatriculasFinancieras().stream()
                    .map(this::mappearMatriculaFinanciera)
                    .collect(Collectors.toList()));
        } else {
            domain.setMatriculasFinancieras(Collections.emptyList());
        }

        // Mapear descuentos con porcentaje derivado del tipo
        if (estudiante.getDescuentos() != null) {
            domain.setDescuentos(estudiante.getDescuentos().stream()
                    .map(this::mappearDescuento)
                    .collect(Collectors.toList()));
        } else {
            domain.setDescuentos(Collections.emptyList());
        }

        // Mapear becas
        if (estudiante.getBecas() != null) {
            domain.setBecas(estudiante.getBecas().stream()
                    .map(this::mappearBeca)
                    .collect(Collectors.toList()));
        } else {
            domain.setBecas(Collections.emptyList());
        }

        domain.setMatriculasAcademicas(Collections.emptyList());
        return domain;
    }

    private MatriculaFinanciera mappearMatriculaFinanciera(MatriculaFinancieraEntity entity) {
        MatriculaFinanciera mf = new MatriculaFinanciera();
        mf.setFechaMatricula(entity.getFechaMatricula());
        mf.setValorMatricula(entity.getValorMatricula());
        mf.setPagada(entity.getPagada());
        if (entity.getObjPeriodoAcademico() != null) {
            PeriodoAcademico pa = new PeriodoAcademico();
            pa.setPeriodo(entity.getObjPeriodoAcademico().getPeriodo());
            pa.setAño(entity.getObjPeriodoAcademico().getAño());
            mf.setObjPeriodoAcademico(pa);
        }
        return mf;
    }

    private Descuento mappearDescuento(DescuentoEntity entity) {
        Descuento d = new Descuento();
        d.setTipoDescuento(entity.getTipoDescuento());
        d.setPorcentaje(derivarPorcentajeDescuento(entity.getTipoDescuento()));
        return d;
    }

    private Beca mappearBeca(BecaEntity entity) {
        Beca b = new Beca();
        b.setResolucion(entity.getTitulo());
        b.setPorcentaje(0f);
        return b;
    }

    /**
     * Deriva el porcentaje de descuento según el tipo.
     * Votación: 10%, Egresado: 5%.
     */
    private Float derivarPorcentajeDescuento(String tipoDescuento) {
        if (tipoDescuento == null) return 0f;
        String tipo = tipoDescuento.toLowerCase();
        if (tipo.contains("votac")) return 0.10f;
        if (tipo.contains("egresad")) return 0.05f;
        return 0f;
    }

    @Override
    public List<Estudiante> mappearDeListaEntityAEstudiante(List<EstudianteEntity> estudiantes) {
        if (estudiantes == null) {
            return Collections.emptyList();
        }
        return estudiantes.stream()
                .map(this::mappearDeEntityAEstudiante)
                .collect(Collectors.toList());
    }

    @Override
    public EstudianteEntity mappearEstudianteAEntity(Estudiante estudiante) {
        if (estudiante == null) {
            return null;
        }
        EstudianteEntity entity = new EstudianteEntity();
        entity.setCodigo(estudiante.getCodigo());
        entity.setNombre(estudiante.getNombre());
        entity.setApellido(estudiante.getApellido());
        entity.setIdentificacion(estudiante.getIdentificacion());
        entity.setCohorte(estudiante.getCohorte());
        entity.setPeriodoIngreso(estudiante.getPeriodoIngreso());
        entity.setSemestreFinanciero(estudiante.getSemestreFinanciero());
        entity.setMatriculasFinancieras(null);
        entity.setDescuentos(null);
        entity.setBecas(null);
        return entity;
    }
}
