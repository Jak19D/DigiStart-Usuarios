package DigiStart.Controller;

import DigiStart.DTO.Output.AulaResponseDTO;
import DigiStart.DTO.Output.ExercicioResponseDTO;
import DigiStart.DTO.Output.ModuloResponseDTO;
import DigiStart.Mapper.AulaMapper;
import DigiStart.Mapper.ExercicioMapper;
import DigiStart.Mapper.ModuloMapper;
import DigiStart.Model.Aula;
import DigiStart.Model.Professor;
import DigiStart.Service.AulaService;
import DigiStart.Service.ExercicioService;
import DigiStart.Service.ModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ConteudoGraphQLController {

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private AulaService aulaService;

    @Autowired
    private ExercicioService exercicioService;

    @Autowired
    private ModuloMapper moduloMapper;

    @Autowired
    private AulaMapper aulaMapper;

    @Autowired
    private ExercicioMapper exercicioMapper;

    @QueryMapping
    public List<ModuloResponseDTO> listarModulos() {
        var modulos = moduloService.listarTodosAtivos();

        return modulos.stream()
                .map(moduloMapper::toResponseDTO)
                .toList();
    }

    @QueryMapping
    public ModuloResponseDTO buscarModuloPorId(@Argument Long id) {
        var modulo = moduloService.buscarPorId(id);
        return moduloMapper.toResponseDTO(modulo);
    }

    @QueryMapping
    public List<AulaResponseDTO> listarAulasPorModulo(@Argument Long moduloId) {
        return listarAulasPorModuloId(moduloId);
    }

    @SchemaMapping(typeName = "ModuloResponseDTO", field = "aulas")
    public List<AulaResponseDTO> aulasDoModulo(ModuloResponseDTO modulo) {
        return listarAulasPorModuloId(modulo.getId());
    }

    private List<AulaResponseDTO> listarAulasPorModuloId(Long moduloId) {
        return aulaService.listarPorModulo(moduloId).stream()
                .map(aulaMapper::toResponseDTO)
                .toList();
    }

    @QueryMapping
    public List<AulaResponseDTO> listarAulas(){
        return aulaService.listar().stream()
                .map(aulaMapper::toResponseDTO)
                .toList();
    }

    @QueryMapping
    public List<ExercicioResponseDTO> listarExerciciosPorAula(@Argument Long aulaId) {
        var exercicios = exercicioService.listarPorAula(aulaId);
        return exercicios.stream()
                .map(exercicioMapper::toResponseDTO)
                .toList();
    }

    @MutationMapping
    public AulaResponseDTO editarAula(@Argument Long id, @Argument String titulo, @Argument String descricao){
        Aula aulaAtualizada = new Aula();
        aulaAtualizada.setTitulo(titulo);
        aulaAtualizada.setDescricao(descricao);
        
        Professor professorProprietario = null;
        
        var aula = aulaService.atualizar(id, aulaAtualizada, professorProprietario);
        return aulaMapper.toResponseDTO(aula);
    }
}