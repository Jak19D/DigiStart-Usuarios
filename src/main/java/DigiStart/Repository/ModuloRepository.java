package DigiStart.Repository;

import DigiStart.Model.Modulo;
import DigiStart.Model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    List<Modulo> findByAtivoTrue();

    List<Modulo> findByProfessor(Professor professor);

    List<Modulo> findByProfessorAndAtivoTrue(Professor professor);
}