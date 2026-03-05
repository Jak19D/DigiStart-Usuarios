
@Transactional
    public Aluno atualizarPerfil(Long alunoId, String nome, String email, String senha, String nickname, String dataNascimento) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado para o ID: " + alunoId));

        User user = aluno.getUser();

        // Atualizar nome do user se fornecido
        if (nome != null && !nome.isEmpty()) {
            user.setNome(nome);
        }

        // Atualizar email se fornecido e diferente
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            validarDominioEmail(email);
            if (userService.existeEmail(email)) {
                throw new ValidacaoException("Este email já está em uso.");
            }
            user.setEmail(email);
        }

        // Atualizar senha se fornecida
        if (senha != null && !senha.isEmpty()) {
            userService.validarForcaSenha(senha);
            user.setSenha(senha);
        }

        // Atualizar nickname se fornecido e diferente
        if (nickname != null && !nickname.isEmpty() && !nickname.equals(aluno.getNickname())) {
            validarNickname(nickname);
            aluno.setNickname(nickname);
        }

        // Atualizar data de nascimento se fornecida
        if (dataNascimento != null && !dataNascimento.isEmpty()) {
            try {
                DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate data = LocalDate.parse(dataNascimento, formatador);
                validarDataNascimento(data);
                validarIdadeMinima(data);
                aluno.setDataNascimento(data);
            } catch (Exception e) {
                throw new ValidacaoException("Formato de data inválido. Use DD/MM/AAAA.");
            }
        }

        // Salvar user primeiro
        userService.salvar(user);
        
        // Depois salvar aluno
        return alunoRepository.save(aluno);
    }
