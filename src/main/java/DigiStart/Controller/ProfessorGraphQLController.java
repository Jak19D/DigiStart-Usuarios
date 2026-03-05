package DigiStart.Controller;

import DigiStart.DTO.Input.ProfessorRequestDTO;
import DigiStart.DTO.Output.ProfessorResponseDTO;
import DigiStart.Mapper.ProfessorMapper;
import DigiStart.Service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProfessorGraphQLController {

    @Autowired
    private ProfessorService professorService;
    
    @Autowired
    private ProfessorMapper professorMapper;

    @QueryMapping
    public ProfessorResponseDTO buscarProfessorPorId(@Argument Long id) {
        var professor = professorService.buscarPorId(id);
        return professorMapper.toResponseDTO(professor);
    }

    @QueryMapping
    public ProfessorResponseDTO buscarProfessorPorEmail(@Argument String email) {
        var professor = professorService.buscarPorEmail(email);
        return professorMapper.toResponseDTO(professor);
    }

    @QueryMapping
    public List<ProfessorResponseDTO> listarProfessores() {
        var professores = professorService.listar();
        return professores.stream()
                .map(professorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @MutationMapping
    public ProfessorResponseDTO cadastrarProfessor(@Argument @Valid ProfessorRequestDTO input) {
        var professor = professorService.salvar(input);
        return professorMapper.toResponseDTO(professor);
    }

    @MutationMapping
    public ProfessorResponseDTO editarProfessor(@Argument Long id, @Argument String nome, @Argument String telefone, @Argument String email) {
        var professor = professorService.atualizarPerfil(id, nome, telefone, email);
        return professorMapper.toResponseDTO(professor);
    }

    @MutationMapping
    public boolean deletarProfessor(@Argument Long id) {
        professorService.deletar(id);
        return true;
    }
}
