package DigiStart.Controller;

import DigiStart.DTO.Output.AulaResponseDTO;
import DigiStart.DTO.Output.ExercicioResponseDTO;
import DigiStart.DTO.Output.ModuloResponseDTO;
import DigiStart.Mapper.AulaMapper;
import DigiStart.Mapper.ExercicioMapper;
import DigiStart.Mapper.ModuloMapper;
import DigiStart.Model.Aula;
import DigiStart.Service.AulaService;
import DigiStart.Service.ExercicioService;
import DigiStart.Service.ModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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
        var aulas = aulaService.listarPorModulo(moduloId);
        return aulas.stream()
                .map(aulaMapper::toResponseDTO)
                .toList();
    }

    @QueryMapping List<Aula> listar(){
        return aulaService.listar();
    }

    @QueryMapping
    public List<ExercicioResponseDTO> listarExerciciosPorAula(@Argument Long aulaId) {
        var exercicios = exercicioService.listarPorAula(aulaId);
        return exercicios.stream()
                .map(exercicioMapper::toResponseDTO)
                .toList();
    }
}