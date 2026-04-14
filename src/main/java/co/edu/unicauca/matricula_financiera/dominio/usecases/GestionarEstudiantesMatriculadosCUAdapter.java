package co.edu.unicauca.matricula_financiera.dominio.usecases;

import co.edu.unicauca.matricula_financiera.aplication.input.GestionarEstudiantesMatriculadosCUIntPort;
import co.edu.unicauca.matricula_financiera.aplication.output.FormateadorResultadosIntPort;
import co.edu.unicauca.matricula_financiera.aplication.output.GestionarEstudiantesMatriculadosGatewayIntPort;
import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.dominio.models.Materia;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;

import java.util.ArrayList;
import java.util.List;

public class GestionarEstudiantesMatriculadosCUAdapter implements GestionarEstudiantesMatriculadosCUIntPort {

    private final GestionarEstudiantesMatriculadosGatewayIntPort objGateway;
    private final FormateadorResultadosIntPort objFormateadorResultados;

    public GestionarEstudiantesMatriculadosCUAdapter(
            GestionarEstudiantesMatriculadosGatewayIntPort objGateway,
            FormateadorResultadosIntPort objFormateadorResultados) {
        this.objGateway = objGateway;
        this.objFormateadorResultados = objFormateadorResultados;
    }

    @Override
    public List<Estudiante> obtenerEstudiantes(PeriodoAcademico periodo) {
        if (periodo == null) {
            objFormateadorResultados.errorReglaNegocioViolada("El periodo académico no puede ser nulo");
            return new ArrayList<>();
        }

        // Verificar existencia del periodo en la BD compartida
        if (!objGateway.existePeriodoAcademico(periodo)) {
            objFormateadorResultados.errorEntidadNoExiste(
                    "El periodo académico " + periodo.getAño() + "-" + periodo.getPeriodo() + " no existe");
            return new ArrayList<>();
        }

        // Resolver el id del periodo
        PeriodoAcademico periodoEnBd = objGateway.findPeriodoByTagAndAnio(
                periodo.getTagPeriodo(), periodo.getAño());

        // Obtener estudiantes del periodo directamente desde la BD compartida
        List<Estudiante> estudiantes = objGateway.obtenerEstudiantesPorPeriodoId(periodoEnBd.getId());

        for (Estudiante e : estudiantes) {
            enriquecer(e, periodo.getPeriodo(), periodo.getAño());
        }
        return estudiantes;
    }

    @Override
    public Estudiante obtenerEstudiante(String codigo, Integer tagPeriodo, Integer anio) {
        if (codigo == null) {
            objFormateadorResultados.errorReglaNegocioViolada("El código del estudiante no puede ser nulo");
            return null;
        }
        if (!objGateway.existeEstudiante(codigo)) {
            objFormateadorResultados.errorEntidadNoExiste("El estudiante con código " + codigo + " no existe");
            return null;
        }
        Estudiante estudiante = objGateway.obtenerEstudiante(codigo);
        if (estudiante != null) {
            // Si no se pasa periodo, usar el activo
            Integer periodoTag = tagPeriodo;
            Integer periodoAnio = anio;
            if (periodoTag == null || periodoAnio == null) {
                PeriodoAcademico periodoActual = objGateway.obtenerPeriodoAcademicoActual();
                if (periodoActual != null) {
                    periodoTag = periodoActual.getPeriodo();
                    periodoAnio = periodoActual.getAño();
                }
            }
            if (periodoTag != null && periodoAnio != null) {
                enriquecer(estudiante, periodoTag, periodoAnio);
            }
        }
        return estudiante;
    }

    @Override
    public List<PeriodoAcademico> obtenerPeriodosAcademicos() {
        return objGateway.obtenerPeriodosAcademicos();
    }

    // -------------------------------------------------------------------------
    // Enriquecimiento desde BD compartida
    // -------------------------------------------------------------------------

    private void enriquecer(Estudiante estudiante, Integer tagPeriodo, Integer anio) {
        if (estudiante.getId() == null) return;

        // Datos personales y semestre académico desde tabla personas + estudiantes
        objGateway.enriquecerDatosPersonales(estudiante);

        // Matrículas académicas (materias) desde tablas matriculas + cursos + asignaturas
        List<MatriculaAcademica> matriculas =
                objGateway.obtenerMatriculasAcademicas(estudiante.getId(), tagPeriodo, anio);
        estudiante.setMatriculasAcademicas(new ArrayList<>(matriculas));

        // Calcular valorEnSMLV
        estudiante.setValorEnSMLV(calcularValorEnSMLV(estudiante));
    }

    private Integer calcularValorEnSMLV(Estudiante estudiante) {
        Integer semestre = estudiante.getSemestreFinanciero();
        if (semestre == null) return null;

        // Semestres 1 al 4 → siempre 6 SMLV
        if (semestre <= 4) return 6;

        // Semestre >= 5: si no tiene materias en el periodo → null (no se proyecta matrícula)
        List<Materia> materias = obtenerMateriasPlanas(estudiante.getMatriculasAcademicas());
        if (materias.isEmpty()) return null;

        // Regla 1b: solo TG2 → 1 SMLV
        boolean soloTG2 = materias.stream().allMatch(m -> esTrabajodeGrado2(m.getMateria()));
        if (soloTG2) return 1;

        // Regla 1c: TG2 + otras → 6 SMLV
        return 6;
    }

    private List<Materia> obtenerMateriasPlanas(List<MatriculaAcademica> matriculas) {
        if (matriculas == null) return List.of();
        List<Materia> result = new ArrayList<>();
        for (MatriculaAcademica ma : matriculas) {
            if (ma.getMaterias() != null) result.addAll(ma.getMaterias());
        }
        return result;
    }

    private boolean esTrabajodeGrado2(String nombre) {
        if (nombre == null) return false;
        String n = nombre.toLowerCase().trim();
        return n.contains("trabajo de grado 2") || n.contains("trabajo de grado ii");
    }
}
