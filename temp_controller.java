package DigiStart.Controller;

import DigiStart.DTO.Input.ProfessorRequestDTO;
import DigiStart.DTO.Output.ProfessorResponseDTO;
import DigiStart.Mapper.ProfessorMapper;
\nimport DigiStart.Mapper.AulaMapper;\nimport DigiStart.Model.Aula;
import DigiStart.Service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProfessorGraphQLController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private ProfessorMapper professorMapper;

    @Autowired
    private ModuloMapper moduloMapper;

    @Autowired
    private AulaService aulaService;

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
                .toList();
    }

    @MutationMapping
    public ProfessorResponseDTO cadastrarProfessor(@Argument @Valid ProfessorRequestDTO input) {
        var professor = professorService.salvar(input);
        return professorMapper.toResponseDTO(professor);
    }



    @MutationMapping
    public ModuloResponseDTO criarModulo(@Argument @Valid ModuloRequestDTO input, @Argument Long professorId) {
        var modulo = moduloService.salvar(input, professorId);
        return moduloMapper.toResponseDTO(modulo);
    }


    @MutationMapping
    public Boolean deletarAula(@Argument Long id) {
        return aulaService.desativarAula(id);
    }

    @MutationMapping
    public Boolean deletarModulo(@Argument Long moduloId, @Argument Long professorId) {
        return moduloService.desativarModuloSeDono(moduloId, professorId);
    }
}
