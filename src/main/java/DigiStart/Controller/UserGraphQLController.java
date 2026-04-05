package DigiStart.Controller;

import DigiStart.Model.User;
import DigiStart.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserGraphQLController {

    @Autowired
    private UserService userService;

    @QueryMapping
    public User autenticarUsuario(@Argument String email, @Argument String senha) {
        return userService.autenticarUsuario(email, senha);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> listarUsers() {
        return userService.listar();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User atualizarUser(@Argument Long id, @Argument String email, @Argument String tipo) {
        User user = new User();
        user.setEmail(email);
        user.setTipo(tipo);
        return userService.atualizar(id, user);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public boolean inativarUser(@Argument Long id) {
        userService.inativar(id);
        return true;
    }

    @QueryMapping
    public boolean existeEmail(@Argument String email) {
        return userService.existeEmail(email);
    }
}
