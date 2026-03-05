package DigiStart.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DigiStart.Model.Professor;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUserId(Long userId);
    Optional<Professor> findByUserEmail(String email);
}