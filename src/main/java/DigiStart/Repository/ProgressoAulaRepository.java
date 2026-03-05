package DigiStart.Repository;

import DigiStart.Model.ProgressoAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressoAulaRepository extends JpaRepository<ProgressoAula, Long> {


    Optional<ProgressoAula> findByAlunoIdAndAulaId(Long alunoId, Long aulaId);

    List<ProgressoAula> findByAlunoId(Long alunoId);

    List<ProgressoAula> findByAlunoIdAndStatus(Long alunoId, ProgressoAula.StatusAula status);
}