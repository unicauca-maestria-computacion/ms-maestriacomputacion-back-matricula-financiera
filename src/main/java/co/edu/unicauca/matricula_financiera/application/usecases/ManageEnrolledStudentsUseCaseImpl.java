package co.edu.unicauca.matricula_financiera.application.usecases;

import co.edu.unicauca.matricula_financiera.domain.models.BecaDescuentoInfo;
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
        return students.stream()
                .peek(s -> {
                    enrich(s, period.getTagPeriodo(), period.getAño());
                    // Calcular semestres DESPUÉS de enrich para no ser pisados por enrichPersonalData
                    int semReal = calculateSemester(s.getPeriodoIngreso(), period.getTagPeriodo(), period.getAño());
                    if (semReal > 0) {
                        s.setSemestreFinanciero(semReal);
                        s.setSemestreAcademico(Math.min(semReal, 4));
                    }
                    // Recalcular SMLV con el semestre financiero correcto
                    s.setValorEnSMLV(calculateSmlv(s));
                })
                .filter(s -> s.getValorEnSMLV() != null)
                .toList();
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
            int semReal = calculateSemester(student.getPeriodoIngreso(), resolvedTag, resolvedYear);
            if (semReal > 0) {
                student.setSemestreFinanciero(semReal);
                student.setSemestreAcademico(Math.min(semReal, 4));
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

    @Override
    public boolean tieneDescuentoVoto(String codigoEstudiante) {
        return gateway.tieneSolicitudCerVotoAprobada(codigoEstudiante);
    }

    private void enrich(Estudiante student, Integer tag, Integer year) {
        if (student.getId() == null) return;
        gateway.enrichPersonalData(student);
        List<MatriculaAcademica> enrollments =
                gateway.findAcademicEnrollments(student.getId(), tag, year);
        student.setMatriculasAcademicas(new ArrayList<>(enrollments));
        // Obtener período completo para tener fechas
        PeriodoAcademico periodo = gateway.findPeriodByTagAndYear(tag, year);
        if (periodo != null) {
            // AUTO-REGISTRO: Asegurar que el estudiante tenga su registro financiero real
            // Si es nuevo o no tiene registro, heredamos su último grupo conocido
            Long ultimoGrupo = gateway.findUltimoGrupoId(student.getId());
            gateway.registrarMatriculaFinanciera(student.getId(), periodo.getId(), ultimoGrupo, false);
            
            if (periodo.getFechaInicio() != null && periodo.getFechaFin() != null) {
                List<BecaDescuentoInfo> becas = gateway.findBecasDescuentosByEstudianteAndPeriodo(
                        student.getId(), periodo.getFechaInicio(), periodo.getFechaFin());
                student.setBecasDescuentos(becas != null ? becas : List.of());
            } else {
                student.setBecasDescuentos(List.of());
            }
        } else {
            student.setBecasDescuentos(List.of());
        }
        
        // Resolver estado de pago global desde la nueva tabla financiera (ya asegurada arriba)
        student.setEstaPago(gateway.findEstadoPago(student.getId(), tag, year));
        // Resolver grupo real
        student.setGrupoNombre(gateway.findGrupoNombre(student.getId(), tag, year));
        // Resolver descuento de voto real
        student.setAplicaVotacion(gateway.tieneSolicitudCerVotoAprobada(student.getCodigo()));
        // valorEnSMLV se recalcula en el caller después de setear semestreFinanciero
    }


    private Integer calculateSmlv(Estudiante student) {
        Integer semester = student.getSemestreFinanciero();
        if (semester == null) return null;
        if (semester <= 4) return 6;
        if (semester >= 9) return 1; // Semester 9+ always pays 1 SMLV according to Acuerdo 044/2012
        List<Materia> subjects = flatSubjects(student.getMatriculasAcademicas());
        if (subjects.isEmpty()) return 6; 
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

    // Calcula el semestre del estudiante basandose en el periodo de ingreso
    private int calculateSemester(String periodoIngreso, Integer tagConsulta, Integer anioConsulta) {
        if (periodoIngreso == null || tagConsulta == null || anioConsulta == null) return 0;
        String[] parts = periodoIngreso.split("-");
        if (parts.length != 2) return 0;
        try {
            int anioIngreso = Integer.parseInt(parts[0].trim());
            int tagIngreso = Integer.parseInt(parts[1].trim());
            int semReal = (anioConsulta - anioIngreso) * 2 + (tagConsulta - tagIngreso) + 1;
            return Math.max(semReal, 0);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
