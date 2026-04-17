package co.edu.unicauca.matricula_financiera.application.usecases;

import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.Materia;
import co.edu.unicauca.matricula_financiera.domain.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.domain.ports.in.ManageEnrolledStudentsUseCase;
import co.edu.unicauca.matricula_financiera.domain.ports.out.ResultFormatterPort;
import co.edu.unicauca.matricula_financiera.domain.ports.out.StudentGatewayPort;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ManageEnrolledStudentsUseCaseImpl implements ManageEnrolledStudentsUseCase {

    private final StudentGatewayPort gateway;
    private final ResultFormatterPort formatter;

    @Override
    public List<Estudiante> getStudentsByPeriod(PeriodoAcademico period) {
        if (period == null) {
            formatter.errorBusinessRuleViolated("validation.period.notNull");
            return new ArrayList<>();
        }
        if (!gateway.existsAcademicPeriod(period)) {
            formatter.errorEntityNotFound("validation.period.notFound",
                    period.getTagPeriodo(), period.getAño());
            return new ArrayList<>();
        }
        PeriodoAcademico resolved = gateway.findPeriodByTagAndYear(
                period.getTagPeriodo(), period.getAño());
        List<Estudiante> students = gateway.findStudentsByPeriodId(resolved.getId());
        students.forEach(s -> enrich(s, period.getTagPeriodo(), period.getAño()));
        return students;
    }

    @Override
    public Estudiante getStudentByCode(String code, Integer tagPeriodo, Integer year) {
        if (code == null) {
            formatter.errorBusinessRuleViolated("validation.student.code.notNull");
            return null;
        }
        if (!gateway.existsStudentByCode(code)) {
            formatter.errorEntityNotFound("validation.student.code.notFound", code);
            return null;
        }
        Estudiante student = gateway.findStudentByCode(code);
        Integer resolvedTag = tagPeriodo;
        Integer resolvedYear = year;
        if (resolvedTag == null || resolvedYear == null) {
            PeriodoAcademico active = gateway.findActivePeriod();
            if (active != null) {
                resolvedTag = active.getTagPeriodo();
                resolvedYear = active.getAño();
            }
        }
        if (resolvedTag != null && resolvedYear != null) {
            enrich(student, resolvedTag, resolvedYear);
        }
        return student;
    }

    @Override
    public List<PeriodoAcademico> getAcademicPeriods() {
        return gateway.findAllPeriods();
    }

    private void enrich(Estudiante student, Integer tag, Integer year) {
        if (student.getId() == null) return;
        gateway.enrichPersonalData(student);
        List<MatriculaAcademica> enrollments =
                gateway.findAcademicEnrollments(student.getId(), tag, year);
        student.setMatriculasAcademicas(new ArrayList<>(enrollments));
        student.setValorEnSMLV(calculateSmlv(student));
    }

    private Integer calculateSmlv(Estudiante student) {
        Integer semester = student.getSemestreFinanciero();
        if (semester == null) return null;
        if (semester <= 4) return 6;
        List<Materia> subjects = flatSubjects(student.getMatriculasAcademicas());
        if (subjects.isEmpty()) return null;
        boolean onlyTg2 = subjects.stream().allMatch(m -> isTg2(m.getMateria()));
        return onlyTg2 ? 1 : 6;
    }

    private List<Materia> flatSubjects(List<MatriculaAcademica> enrollments) {
        if (enrollments == null) return List.of();
        return enrollments.stream()
                .filter(e -> e.getMaterias() != null)
                .flatMap(e -> e.getMaterias().stream())
                .toList();
    }

    private boolean isTg2(String name) {
        if (name == null) return false;
        String n = name.toLowerCase().trim();
        return n.contains("trabajo de grado 2") || n.contains("trabajo de grado ii");
    }
}
