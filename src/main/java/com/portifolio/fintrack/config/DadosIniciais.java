package com.portifolio.fintrack.config;

import com.portifolio.fintrack.model.Usuario;
import com.portifolio.fintrack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean demoEnabled;

    public DadosIniciais(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.demo.enabled:true}") boolean demoEnabled
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.demoEnabled = demoEnabled;
    }

    @Override
    public void run(String... args) {
        if (!demoEnabled) {
            return;
        }

        usuarioRepository.findByEmail("lucas@email.com")
                .orElseGet(this::criarUsuarioDemo);
    }

    private Usuario criarUsuarioDemo() {
        Usuario usuario = new Usuario();
        usuario.setNome("Lucas");
        usuario.setEmail("lucas@email.com");
        usuario.setSenha(passwordEncoder.encode("123456"));
        return usuarioRepository.save(usuario);
    }

}
