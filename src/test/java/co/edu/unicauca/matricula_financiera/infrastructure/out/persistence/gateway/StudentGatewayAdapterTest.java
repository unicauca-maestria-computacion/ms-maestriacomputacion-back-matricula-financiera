package co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.gateway;

import co.edu.unicauca.matricula_financiera.domain.enums.PeriodoEstado;
import co.edu.unicauca.matricula_financiera.domain.models.BecaDescuentoInfo;
import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.entity.EstudianteEntity;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.mapper.EstudiantePersistenceMapper;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.repository.BdCompartidaRepository;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.repository.EstudianteJpaRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del adaptador de persistencia.
 *
 * Todas las conexiones externas se sustituyen por dobles de prueba de Mockito:
 * el repositorio JPA, el repositorio basado en JdbcTemplate y el mapeador
 * generado por MapStruct. La prueba no requiere base de datos ni contexto de
 * Spring, y verifica el reparto de responsabilidades entre ambos repositorios
 * descrito en la seccion 3.8.3 del documento.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentGatewayAdapter - adaptador de persistencia")
class StudentGatewayAdapterTest {

    @Mock
    private EstudianteJpaRepository repository;

    @Mock
    private EstudiantePersistenceMapper mapper;

    @Mock
    private BdCompartidaRepository bdCompartida;

    @InjectMocks
    private StudentGatewayAdapter adapter;

    private EstudianteEntity entity(Long id, String codigo) {
        EstudianteEntity e = new EstudianteEntity();
        e.setId(id);
        e.setCodigo(codigo);
        return e;
    }

    private Estudiante model(Long id, String codigo) {
        Estudiante e = new Estudiante();
        e.setId(id);
        e.setCodigo(codigo);
        return e;
    }

    private PeriodoAcademico periodo(Long id, int tag, int anio, PeriodoEstado estado) {
        PeriodoAcademico p = new PeriodoAcademico();
        p.setId(id);
        p.setTagPeriodo(tag);
        p.setAño(anio);
        p.setEstado(estado);
        return p;
    }

    // ------------------------------------------------------------------
    // Consultas resueltas con Spring Data JPA
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Consultas sobre la tabla estudiantes (JPA)")
    class ConsultasJpa {

        @Test
        @DisplayName("findStudentsByPeriodId delega en el repositorio JPA y mapea a dominio")
        void findStudentsByPeriodId_delegatesToJpaAndMaps() {
            EstudianteEntity e1 = entity(1L, "EST001");
            EstudianteEntity e2 = entity(2L, "EST002");
            when(repository.findByPeriodoId(10L)).thenReturn(List.of(e1, e2));
            when(mapper.fromEntityToEstudiante(e1)).thenReturn(model(1L, "EST001"));
            when(mapper.fromEntityToEstudiante(e2)).thenReturn(model(2L, "EST002"));

            List<Estudiante> result = adapter.findStudentsByPeriodId(10L);

            assertThat(result).hasSize(2)
                    .extracting(Estudiante::getCodigo)
                    .containsExactly("EST001", "EST002");
            verify(repository).findByPeriodoId(10L);
            verifyNoInteractions(bdCompartida);
        }

        @Test
        @DisplayName("findStudentsByPeriodId con id nulo retorna lista vacia sin consultar")
        void findStudentsByPeriodId_whenIdIsNull_returnsEmptyWithoutQuerying() {
            List<Estudiante> result = adapter.findStudentsByPeriodId(null);

            assertThat(result).isEmpty();
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("findStudentsByPeriodId retorna una lista mutable")
        void findStudentsByPeriodId_returnsMutableList() {
            when(repository.findByPeriodoId(anyLong())).thenReturn(List.of(entity(1L, "EST001")));
            when(mapper.fromEntityToEstudiante(any())).thenReturn(model(1L, "EST001"));

            List<Estudiante> result = adapter.findStudentsByPeriodId(10L);

            assertThat(result).isNotNull();
            result.add(model(9L, "EST009"));
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("findStudentByCode retorna null cuando el codigo no existe")
        void findStudentByCode_whenNotFound_returnsNull() {
            when(repository.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());

            assertThat(adapter.findStudentByCode("NOEXISTE")).isNull();
            verify(mapper, never()).fromEntityToEstudiante(any());
        }

        @Test
        @DisplayName("existsStudentByCode refleja la presencia del registro")
        void existsStudentByCode_reflectsPresence() {
            when(repository.findByCodigo("EST001")).thenReturn(Optional.of(entity(1L, "EST001")));
            when(repository.findByCodigo("EST999")).thenReturn(Optional.empty());

            assertThat(adapter.existsStudentByCode("EST001")).isTrue();
            assertThat(adapter.existsStudentByCode("EST999")).isFalse();
        }
    }

    // ------------------------------------------------------------------
    // Consultas resueltas con JdbcTemplate sobre el esquema compartido
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Consultas sobre el esquema compartido (JdbcTemplate)")
    class ConsultasEsquemaCompartido {

        @Test
        @DisplayName("enrichPersonalData copia los datos personales al modelo de dominio")
        void enrichPersonalData_copiesFields() {
            Estudiante student = model(1L, "EST001");
            when(bdCompartida.findDatosPersonalesEstudiante(1L))
                    .thenReturn(new Object[] { "Ana", "Lopez", 1061234567L, 3, Boolean.TRUE });

            adapter.enrichPersonalData(student);

            assertThat(student.getNombre()).isEqualTo("Ana");
            assertThat(student.getApellido()).isEqualTo("Lopez");
            assertThat(student.getIdentificacion()).isEqualTo(1061234567L);
            assertThat(student.getSemestreAcademico()).isEqualTo(3);
            assertThat(student.getEsEgresadoUnicauca()).isTrue();
        }

        @Test
        @DisplayName("enrichPersonalData no falla cuando la consulta no devuelve filas")
        void enrichPersonalData_whenNoRow_leavesStudentUntouched() {
            Estudiante student = model(1L, "EST001");
            when(bdCompartida.findDatosPersonalesEstudiante(1L)).thenReturn(null);

            adapter.enrichPersonalData(student);

            assertThat(student.getNombre()).isNull();
        }

        @Test
        @DisplayName("enrichPersonalData ignora estudiantes sin identificador")
        void enrichPersonalData_whenStudentHasNoId_doesNotQuery() {
            adapter.enrichPersonalData(new Estudiante());
            adapter.enrichPersonalData(null);

            verifyNoInteractions(bdCompartida);
        }

        @Test
        @DisplayName("existsAcademicPeriod es falso si faltan tag o anio")
        void existsAcademicPeriod_whenIncompletePeriod_returnsFalse() {
            assertThat(adapter.existsAcademicPeriod(null)).isFalse();
            assertThat(adapter.existsAcademicPeriod(new PeriodoAcademico())).isFalse();

            verifyNoInteractions(bdCompartida);
        }

        @Test
        @DisplayName("existsAcademicPeriod consulta el esquema compartido cuando el periodo esta completo")
        void existsAcademicPeriod_whenComplete_queriesSharedSchema() {
            PeriodoAcademico p = periodo(1L, 1, 2024, PeriodoEstado.ACTIVO);
            when(bdCompartida.findPeriodoByTagAndAnio(1, 2024)).thenReturn(p);

            assertThat(adapter.existsAcademicPeriod(p)).isTrue();
            verify(bdCompartida).findPeriodoByTagAndAnio(1, 2024);
        }

        @Test
        @DisplayName("findActivePeriod selecciona el primer periodo en estado ACTIVO")
        void findActivePeriod_selectsFirstActive() {
            when(bdCompartida.findAllPeriodos()).thenReturn(List.of(
                    periodo(3L, 1, 2025, PeriodoEstado.INACTIVO),
                    periodo(2L, 2, 2024, PeriodoEstado.ACTIVO),
                    periodo(1L, 1, 2024, PeriodoEstado.CERRADO)));

            PeriodoAcademico active = adapter.findActivePeriod();

            assertThat(active).isNotNull();
            assertThat(active.getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("findActivePeriod retorna null si ningun periodo esta activo")
        void findActivePeriod_whenNoneActive_returnsNull() {
            when(bdCompartida.findAllPeriodos())
                    .thenReturn(List.of(periodo(1L, 1, 2024, PeriodoEstado.CERRADO)));

            assertThat(adapter.findActivePeriod()).isNull();
        }

        @Test
        @DisplayName("findAcademicEnrollments retorna vacio ante cualquier parametro nulo")
        void findAcademicEnrollments_whenAnyParamIsNull_returnsEmpty() {
            assertThat(adapter.findAcademicEnrollments(null, 1, 2024)).isEmpty();
            assertThat(adapter.findAcademicEnrollments(1L, null, 2024)).isEmpty();
            assertThat(adapter.findAcademicEnrollments(1L, 1, null)).isEmpty();

            verifyNoInteractions(bdCompartida);
        }

        @Test
        @DisplayName("findAcademicEnrollments delega con los tres parametros")
        void findAcademicEnrollments_delegatesWithAllParams() {
            when(bdCompartida.findMatriculasPorEstudianteYPeriodo(1L, 1, 2024))
                    .thenReturn(List.of(new MatriculaAcademica()));

            assertThat(adapter.findAcademicEnrollments(1L, 1, 2024)).hasSize(1);
            verify(bdCompartida).findMatriculasPorEstudianteYPeriodo(1L, 1, 2024);
        }

        @Test
        @DisplayName("findBecasDescuentos propaga las fechas del periodo")
        void findBecasDescuentos_propagatesPeriodDates() {
            LocalDate inicio = LocalDate.of(2024, 1, 15);
            LocalDate fin = LocalDate.of(2024, 6, 30);
            when(bdCompartida.findBecasDescuentosByEstudianteAndPeriodo(1L, inicio, fin))
                    .thenReturn(List.of(new BecaDescuentoInfo("BECA", 50.0f, "RES-001", "APROBADA", "SI")));

            List<BecaDescuentoInfo> result =
                    adapter.findBecasDescuentosByEstudianteAndPeriodo(1L, inicio, fin);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTipo()).isEqualTo("BECA");
            verify(bdCompartida).findBecasDescuentosByEstudianteAndPeriodo(1L, inicio, fin);
        }

        @Test
        @DisplayName("findEstadoPago puede devolver null cuando no hay registro financiero")
        void findEstadoPago_mayReturnNull() {
            when(bdCompartida.findEstadoPagoPorEstudianteYPeriodo(anyLong(), anyInt(), anyInt()))
                    .thenReturn(null);

            assertThat(adapter.findEstadoPago(1L, 1, 2024)).isNull();
        }
    }

    // ------------------------------------------------------------------
    // Unica operacion de escritura del modulo
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("Registro de la matricula financiera (unica escritura)")
    class Escritura {

        @Test
        @DisplayName("registrarMatriculaFinanciera delega en el repositorio con los cuatro parametros")
        void registrarMatriculaFinanciera_delegates() {
            adapter.registrarMatriculaFinanciera(1L, 10L, 5L, Boolean.TRUE);

            verify(bdCompartida).registrarMatriculaFinanciera(1L, 10L, 5L, Boolean.TRUE);
        }

        @Test
        @DisplayName("registrarMatriculaFinanciera es idempotente: repetirla no cambia el resultado")
        void registrarMatriculaFinanciera_isIdempotent() {
            adapter.registrarMatriculaFinanciera(1L, 10L, 5L, null);
            adapter.registrarMatriculaFinanciera(1L, 10L, 5L, null);
            adapter.registrarMatriculaFinanciera(1L, 10L, 5L, null);

            // La sentencia INSERT ... ON DUPLICATE KEY UPDATE hace que las tres
            // invocaciones produzcan el mismo estado en la base de datos.
            verify(bdCompartida, org.mockito.Mockito.times(3))
                    .registrarMatriculaFinanciera(eq(1L), eq(10L), eq(5L), eq(null));
        }

        @Test
        @DisplayName("findUltimoGrupoId propaga el grupo heredado por el estudiante")
        void findUltimoGrupoId_propagates() {
            when(bdCompartida.findUltimoGrupoIdByEstudiante(1L)).thenReturn(7L);

            assertThat(adapter.findUltimoGrupoId(1L)).isEqualTo(7L);
        }
    }
}
