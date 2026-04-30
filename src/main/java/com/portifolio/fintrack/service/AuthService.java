package com.portifolio.fintrack.service;

import com.portifolio.fintrack.dto.AuthRequest;
import com.portifolio.fintrack.dto.AuthResponse;
import com.portifolio.fintrack.dto.PerfilRequest;
import com.portifolio.fintrack.dto.RegistroRequest;
import com.portifolio.fintrack.exception.RegraNegocioException;
import com.portifolio.fintrack.model.Usuario;
import com.portifolio.fintrack.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse registrar(RegistroRequest request) {
        String email = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException("Este email ja esta cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(request.senha()));

        return toResponse(usuarioRepository.save(usuario));
    }

    public AuthResponse login(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new RegraNegocioException("Email ou senha invalidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new RegraNegocioException("Email ou senha invalidos");
        }

        return toResponse(usuario);
    }

    public AuthResponse atualizarPerfil(Long usuarioId, PerfilRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado"));
        String email = request.email().trim().toLowerCase();

        usuarioRepository.findByEmail(email)
                .filter(usuarioEncontrado -> !usuarioEncontrado.getId().equals(usuarioId))
                .ifPresent(usuarioEncontrado -> {
                    throw new RegraNegocioException("Este email ja esta cadastrado");
                });

        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);

        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    private AuthResponse toResponse(Usuario usuario) {
        return new AuthResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
