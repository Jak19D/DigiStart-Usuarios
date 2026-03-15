package DigiStart.Repository;

import DigiStart.Model.Modulo;
import DigiStart.Model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    List<Modulo> findByAtivoTrue();

    List<Modulo> findByProfessor(Professor professor);

    List<Modulo> findByProfessorAndAtivoTrue(Professor professor);

    @Query("SELECT a.modulo FROM Aula a " +
           "JOIN ProgressoAula p ON p.aula.id = a.id " +
           "WHERE p.aluno.id = :alunoId " +
           "ORDER BY a.ordem ASC")
    Optional<Modulo> findModuloByAlunoId(@Param("alunoId") Long alunoId);
}