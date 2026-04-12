package DigiStart.Repository;

import DigiStart.Model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepositoryShard2 extends JpaRepository<Professor, Long> {

    Optional<Professor> findByUserId(Long userId);

    @Query("SELECT p FROM Professor p JOIN p.user u WHERE u.email = :email")
    Optional<Professor> findByUserEmail(@Param("email") String email);

    List<Professor> findAll();
}
