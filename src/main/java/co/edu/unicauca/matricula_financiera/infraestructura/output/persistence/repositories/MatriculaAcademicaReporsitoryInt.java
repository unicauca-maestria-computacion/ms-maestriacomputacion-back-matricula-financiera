package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Nota: En el diagrama aparece ConfiguracionReporteFinancieroEntity, pero parece ser un error.
// Se crea el repositorio para MatriculaAcademicaEntity aunque no esté definida en el diagrama.
// Si necesitas MatriculaAcademicaEntity, se puede crear después.
@Repository
public interface MatriculaAcademicaReporsitoryInt extends JpaRepository<Object, Long> {
    // TODO: Cambiar Object por MatriculaAcademicaEntity cuando se cree la entidad
}

