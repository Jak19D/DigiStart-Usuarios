package DigiStart.Repository;

import DigiStart.Model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByUserId(Long userId);

    Optional<Aluno> findByNickname(String nickname);

    @Query("SELECT a FROM Aluno a JOIN a.user u WHERE u.email = :email")
    Optional<Aluno> findByUserEmail(@Param("email") String email);

    @Query("""
        SELECT a FROM Aluno a 
        JOIN a.user u 
        WHERE LOWER(a.nickname) LIKE LOWER(CONCAT('%', :termo, '%')) 
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%'))
    """)
    List<Aluno> buscarPorNomeOuEmail(@Param("termo") String termo);
}