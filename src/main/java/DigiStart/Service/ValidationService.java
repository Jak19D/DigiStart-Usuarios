package DigiStart.Service;

import DigiStart.Exceptions.ValidacaoException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    public void validarDominioEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|email\\.com)$";
        if (!Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE).matcher(email).matches()) {
            throw new ValidacaoException("E-mail inválido. O domínio deve ser @gmail.com ou @email.com.");
        }
    }

    public void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new ValidacaoException("A data de nascimento não pode ser uma data futura.");
        }
        if (Period.between(dataNascimento, LocalDate.now()).getYears() > 120) {
            throw new ValidacaoException("A data de nascimento é inválida (limite de 120 anos).");
        }
    }

    public void validarIdadeMinima(LocalDate dataNascimento, int idadeMinima) {
        if (Period.between(dataNascimento, LocalDate.now()).getYears() < idadeMinima) {
            throw new ValidacaoException("Você precisa ter " + idadeMinima + " anos ou mais para se cadastrar.");
        }
    }

    public void validarIdadeMinimaAluno(LocalDate dataNascimento) {
        validarIdadeMinima(dataNascimento, 10);
    }

    public void validarNome(String nome, int tamanhoMinimo, int tamanhoMaximo) {
        if (nome == null || nome.length() < tamanhoMinimo) {
            throw new ValidacaoException("O nome completo deve ter no mínimo " + tamanhoMinimo + " caracteres.");
        }
        if (nome.length() > tamanhoMaximo) {
            throw new ValidacaoException("O nome completo inserido ultrapassa o limite de " + tamanhoMaximo + " caracteres.");
        }
    }

    public void validarNomeProfessor(String nome) {
        validarNome(nome, 3, 150);
    }

    public void validarNickname(String nickname) {
        if (nickname.length() < 3 || nickname.length() > 15) {
            throw new ValidacaoException("O Nickname deve ter entre 3 e 15 caracteres.");
        }
    }

    public void validarForcaSenha(String senha) {
        if (senha == null || senha.isEmpty()) {
            throw new ValidacaoException("A senha é obrigatória.");
        }

        if (senha.length() < 8 || senha.length() > 16) {
            throw new ValidacaoException("A senha deve ter de 8 a 16 caracteres.");
        }

        if (!senha.matches(".*\\d.*")) {
            throw new ValidacaoException("A senha deve ter pelo menos 1 número.");
        }

        String specialChars = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";
        if (!senha.matches(specialChars)) {
            throw new ValidacaoException("A senha deve ter pelo menos 1 caractere especial.");
        }

        if (!senha.matches(".*[A-Z].*")) {
            throw new ValidacaoException("A senha deve ter pelo menos 1 letra maiúscula.");
        }
    }

    public void validarECaminhoCurriculo(org.springframework.web.multipart.MultipartFile curriculo) {
        if (curriculo == null || curriculo.isEmpty()) {
            throw new ValidacaoException("O currículo em PDF é obrigatório.");
        }
        if (!"application/pdf".equals(curriculo.getContentType())) {
            throw new ValidacaoException("Só são aceitos arquivos no formato PDF.");
        }
    }

    public String getCaminhoCurriculo(org.springframework.web.multipart.MultipartFile curriculo) {
        return "/storage/curriculos/" + curriculo.getOriginalFilename();
    }
}
