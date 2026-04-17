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
        students.forEach(s -> {
            enrich(s, period.getTagPeriodo(), period.getAño());
            // Calcular semestres DESPUÉS de enrich para no ser pisados por enrichPersonalData
            int semestre = calculateSemester(s.getPeriodoIngreso(), period.getTagPeriodo(), period.getAño());
            if (semestre > 0) {
                s.setSemestreFinanciero(semestre);
                s.setSemestreAcademico(semestre);
            }
            // Recalcular SMLV con el semestre financiero correcto
            s.setValorEnSMLV(calculateSmlv(s));
        });
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
            // Calcular semestres dinámicamente DESPUÉS de enrich para no ser pisados
            int semestre = calculateSemester(student.getPeriodoIngreso(), resolvedTag, resolvedYear);
            if (semestre > 0) {
                student.setSemestreFinanciero(semestre);
                student.setSemestreAcademico(semestre);
            }
            // Recalcular SMLV con el semestre financiero correcto
            student.setValorEnSMLV(calculateSmlv(student));
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
        // valorEnSMLV se recalcula en el caller después de setear semestreFinanciero
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

    /**
     * Calcula el semestre del estudiante en función de su período de ingreso
     * y el período que se está consultando.
     *
     * Fórmula: semestre = (añoConsulta - añoIngreso) * 2 + (tagConsulta - tagIngreso) + 1
     *
     * Ejemplos:
     *   ingreso 2024-1, consulta 2024-1 → semestre 1
     *   ingreso 2024-1, consulta 2024-2 → semestre 2
     *   ingreso 2024-1, consulta 2025-1 → semestre 3
     *   ingreso 2024-1, consulta 2025-2 → semestre 4
     *
     * @param periodoIngreso formato "YYYY-N" (ej: "2024-1")
     * @param tagConsulta    tagPeriodo del período consultado (1 o 2)
     * @param añoConsulta    año del período consultado
     * @return semestre calculado, o 0 si no se puede calcular
     */
    private int calculateSemester(String periodoIngreso, Integer tagConsulta, Integer añoConsulta) {
        if (periodoIngreso == null || tagConsulta == null || añoConsulta == null) return 0;
        String[] parts = periodoIngreso.split("-");
        if (parts.length != 2) return 0;
        try {
            int añoIngreso = Integer.parseInt(parts[0].trim());
            int tagIngreso = Integer.parseInt(parts[1].trim());
            int semestre = (añoConsulta - añoIngreso) * 2 + (tagConsulta - tagIngreso) + 1;
            if (semestre <= 0) return 0;
            // El semestre académico no supera 4 (máximo de la maestría)
            return Math.min(semestre, 4);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
