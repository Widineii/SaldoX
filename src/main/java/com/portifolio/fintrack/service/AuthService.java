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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final boolean exibirCodigoQuandoSemSmtp;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Autowired(required = false) JavaMailSender mailSender,
            @Value("${app.mail.from:}") String mailFrom,
            @Value("${app.recuperacao.exibir-codigo-quando-sem-smtp:true}") boolean exibirCodigoQuandoSemSmtp
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.exibirCodigoQuandoSemSmtp = exibirCodigoQuandoSemSmtp;
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

        if (!senhaConfere(request.senha(), usuario)) {
            throw new RegraNegocioException("Email ou senha invalidos");
        }

        return toResponse(usuario);
    }

    private boolean senhaConfere(String senhaInformada, Usuario usuario) {
        String senhaSalva = usuario.getSenha();

        if (senhaSalva == null || senhaSalva.isBlank()) {
            return false;
        }

        if (senhaPareceBCrypt(senhaSalva)) {
            return passwordEncoder.matches(senhaInformada, senhaSalva);
        }

        if (!senhaSalva.equals(senhaInformada)) {
            return false;
        }

        usuario.setSenha(passwordEncoder.encode(senhaInformada));
        usuarioRepository.save(usuario);
        log.info("Senha legada migrada para BCrypt no login: usuarioId={}", usuario.getId());
        return true;
    }

    private boolean senhaPareceBCrypt(String senha) {
        return senha.startsWith("$2a$") || senha.startsWith("$2b$") || senha.startsWith("$2y$");
    }

    public MensagemResponse solicitarRecuperacao(RecuperacaoSenhaRequest request) {
        MensagemResponse respostaUsuarioExisteOuEmail = new MensagemResponse(
                "Se esse email existir em nosso cadastro, voce recebera as instrucoes para redefinir a senha. Verifique a caixa de entrada e spam.",
                null);

        Optional<Usuario> encontrado = usuarioRepository.findByEmail(request.email().trim().toLowerCase());
        if (encontrado.isEmpty()) {
            return respostaUsuarioExisteOuEmail;
        }

        Usuario usuario = encontrado.get();
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        usuario.setResetToken(token);
        usuario.setResetTokenExpiracao(LocalDateTime.now().plusMinutes(20));
        usuarioRepository.save(usuario);

        if (mailSenderDisponivel() && tentarEnviarEmailRecuperacao(usuario.getEmail(), usuario.getNome(), token)) {
            return respostaUsuarioExisteOuEmail;
        }

        if (exibirCodigoQuandoSemSmtp) {
            return new MensagemResponse(
                    "Codigo gerado (ambiente local sem servidor de email configurado — use apenas em desenvolvimento).",
                    token);
        }

        log.warn("Recuperacao de senha sem SMTP: email={} codigo={} (nao retornado na resposta HTTP)", usuario.getEmail(), token);
        return respostaUsuarioExisteOuEmail;
    }

    private boolean mailSenderDisponivel() {
        return mailSender != null && StringUtils.hasText(mailFrom);
    }

    private boolean tentarEnviarEmailRecuperacao(String destino, String nome, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom.trim());
            message.setTo(destino);
            message.setSubject("SaldoX - Codigo para redefinir senha");
            message.setText("Ola " + nome + ",\n\nSeu codigo para redefinir a senha e: "
                    + token + "\n\nEle expira em 20 minutos.\nSe voce nao solicitou, ignore este email.");
            mailSender.send(message);
            return true;
        } catch (MailException exception) {
            throw new RegraNegocioException("Nao foi possivel enviar o email neste momento. Tente mais tarde.");
        }
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
