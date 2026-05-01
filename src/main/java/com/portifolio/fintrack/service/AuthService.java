package com.portifolio.fintrack.service;

import com.portifolio.fintrack.dto.AuthRequest;
import com.portifolio.fintrack.dto.AuthResponse;
import com.portifolio.fintrack.dto.MensagemResponse;
import com.portifolio.fintrack.dto.PerfilRequest;
import com.portifolio.fintrack.dto.RecuperacaoSenhaRequest;
import com.portifolio.fintrack.dto.RedefinirSenhaRequest;
import com.portifolio.fintrack.dto.RegistroRequest;
import com.portifolio.fintrack.exception.RegraNegocioException;
import com.portifolio.fintrack.model.Usuario;
import com.portifolio.fintrack.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    public MensagemResponse solicitarRecuperacao(RecuperacaoSenhaRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new RegraNegocioException("Email nao encontrado"));
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        usuario.setResetToken(token);
        usuario.setResetTokenExpiracao(LocalDateTime.now().plusMinutes(20));
        usuarioRepository.save(usuario);

        return new MensagemResponse("Codigo gerado para redefinir a senha. Em producao, este codigo seria enviado por email.", token);
    }

    public MensagemResponse redefinirSenha(RedefinirSenhaRequest request) {
        Usuario usuario = usuarioRepository.findByResetToken(request.token().trim().toUpperCase())
                .orElseThrow(() -> new RegraNegocioException("Codigo invalido"));

        if (usuario.getResetTokenExpiracao() == null || usuario.getResetTokenExpiracao().isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Codigo expirado");
        }

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuario.setResetToken(null);
        usuario.setResetTokenExpiracao(null);
        usuarioRepository.save(usuario);

        return new MensagemResponse("Senha redefinida com sucesso.", null);
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

    public AuthResponse salvarAvatar(Long usuarioId, MultipartFile arquivo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado"));

        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException("Selecione uma imagem para o avatar");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RegraNegocioException("O avatar precisa ser uma imagem");
        }

        if (arquivo.getSize() > 1_000_000) {
            throw new RegraNegocioException("A imagem deve ter no maximo 1MB");
        }

        try {
            String base64 = Base64.getEncoder().encodeToString(arquivo.getBytes());
            usuario.setAvatarUrl("data:" + contentType + ";base64," + base64);
            return toResponse(usuarioRepository.save(usuario));
        } catch (IOException exception) {
            throw new RegraNegocioException("Nao foi possivel salvar o avatar");
        }
    }

    private AuthResponse toResponse(Usuario usuario) {
        return new AuthResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                jwtService.gerarToken(usuario),
                usuario.getAvatarUrl()
        );
    }
}
