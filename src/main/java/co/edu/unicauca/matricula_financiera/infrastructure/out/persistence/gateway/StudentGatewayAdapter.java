package co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.gateway;

import co.edu.unicauca.matricula_financiera.domain.enums.PeriodoEstado;
import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.domain.ports.out.StudentGatewayPort;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.entity.EstudianteEntity;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.mapper.EstudiantePersistenceMapper;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.repository.BdCompartidaRepository;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.repository.EstudianteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentGatewayAdapter implements StudentGatewayPort {

    private final EstudianteJpaRepository repository;
    private final EstudiantePersistenceMapper mapper;
    private final BdCompartidaRepository bdCompartida;

    @Override
    public List<Estudiante> findStudentsByPeriodId(Long periodId) {
        if (periodId == null) return new ArrayList<>();
        return new ArrayList<>(repository.findByPeriodoId(periodId).stream()
                .map(mapper::fromEntityToEstudiante)
                .toList());
    }

    @Override
    public Estudiante findStudentByCode(String code) {
        Optional<EstudianteEntity> entity = repository.findByCodigo(code);
        return entity.map(mapper::fromEntityToEstudiante).orElse(null);
    }

    @Override
    public boolean existsStudentByCode(String code) {
        return repository.findByCodigo(code).isPresent();
    }

    @Override
    public void enrichPersonalData(Estudiante student) {
        if (student == null || student.getId() == null) return;
        Object[] datos = bdCompartida.findDatosPersonalesEstudiante(student.getId());
        if (datos == null) return;
        student.setNombre((String) datos[0]);
        student.setApellido((String) datos[1]);
        Object idNum = datos[2];
        if (idNum != null) student.setIdentificacion(((Number) idNum).longValue());
        Object semAcad = datos[3];
        if (semAcad != null) student.setSemestreAcademico(((Number) semAcad).intValue());
        student.setEsEgresadoUnicauca((Boolean) datos[4]);
    }

    @Override
    public boolean existsAcademicPeriod(PeriodoAcademico period) {
        if (period == null || period.getTagPeriodo() == null || period.getAño() == null) return false;
        return bdCompartida.findPeriodoByTagAndAnio(period.getTagPeriodo(), period.getAño()) != null;
    }

    @Override
    public PeriodoAcademico findPeriodByTagAndYear(Integer tag, Integer year) {
        return bdCompartida.findPeriodoByTagAndAnio(tag, year);
    }

    @Override
    public List<PeriodoAcademico> findAllPeriods() {
        return bdCompartida.findAllPeriodos();
    }

    @Override
    public PeriodoAcademico findActivePeriod() {
        return bdCompartida.findAllPeriodos().stream()
                .filter(p -> p.getEstado() == PeriodoEstado.ACTIVO)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<MatriculaAcademica> findAcademicEnrollments(Long studentId, Integer tag, Integer year) {
        if (studentId == null || tag == null || year == null) return new ArrayList<>();
        return bdCompartida.findMatriculasPorEstudianteYPeriodo(studentId, tag, year);
    }

    @Override
    public boolean tieneSolicitudCerVotoAprobada(String codigoEstudiante) {
        return bdCompartida.tieneSolicitudCerVotoAprobada(codigoEstudiante);
    }
}
