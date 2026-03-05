package DigiStart.Controller;

import DigiStart.DTO.Input.AlunoRequestDTO;
import DigiStart.DTO.Output.AlunoResponseDTO;
import DigiStart.Mapper.AlunoMapper;
import DigiStart.Service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AlunoGraphQLController {

    @Autowired
    private AlunoService alunoService;
    
    @Autowired
    private AlunoMapper alunoMapper;

    @QueryMapping
    public AlunoResponseDTO buscarAlunoPorId(@Argument Long id) {
        var aluno = alunoService.buscarPorId(id);
        return alunoMapper.toResponseDTO(aluno);
    }

    @QueryMapping
    public AlunoResponseDTO buscarAlunoPorEmail(@Argument String email) {
        var aluno = alunoService.buscarPorEmail(email);
        return alunoMapper.toResponseDTO(aluno);
    }

    @QueryMapping
    public List<AlunoResponseDTO> buscarAlunoPorNome(@Argument String nome) {
        var alunos = alunoService.buscarPorNome(nome);
        return alunos.stream()
                .map(alunoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AlunoResponseDTO> listarTodosAlunos() {
        var alunos = alunoService.listarTodos();
        return alunos.stream()
                .map(alunoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @MutationMapping
    public AlunoResponseDTO cadastrarAluno(@Argument @Valid AlunoRequestDTO input) {
        var aluno = alunoService.salvar(input);
        return alunoMapper.toResponseDTO(aluno);
    }

    @MutationMapping
    public AlunoResponseDTO editarAluno(@Argument Long id, @Argument String nome, @Argument String email, @Argument String senha, @Argument String nickname, @Argument String dataNascimento) {
        var aluno = alunoService.atualizarPerfil(id, nome, email, senha, nickname, dataNascimento);
        return alunoMapper.toResponseDTO(aluno);
    }

    @MutationMapping
    public boolean deletarAluno(@Argument Long id) {
        alunoService.deletar(id);
        return true;
    }
}

