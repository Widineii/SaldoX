package com.portifolio.fintrack.service;

import com.portifolio.fintrack.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    private final ObjectMapper objectMapper;
    private final String secret;
    private final long expiracaoHoras;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${app.jwt.secret:saldox-demo-secret-change-in-production}") String secret,
            @Value("${app.jwt.expiracao-horas:8}") long expiracaoHoras
    ) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.expiracaoHoras = expiracaoHoras;
    }

    public String gerarToken(Usuario usuario) {
        try {
            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", usuario.getEmail());
            payload.put("uid", usuario.getId());
            payload.put("exp", Instant.now().plusSeconds(expiracaoHoras * 3600).getEpochSecond());

            String headerBase64 = base64Url(objectMapper.writeValueAsBytes(header));
            String payloadBase64 = base64Url(objectMapper.writeValueAsBytes(payload));
            String assinatura = assinar(headerBase64 + "." + payloadBase64);
            return headerBase64 + "." + payloadBase64 + "." + assinatura;
        } catch (Exception exception) {
            throw new IllegalStateException("Nao foi possivel gerar o token JWT", exception);
        }
    }

    public Optional<JwtUsuario> validar(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return Optional.empty();
            }

            String conteudoAssinado = partes[0] + "." + partes[1];
            if (!assinar(conteudoAssinado).equals(partes[2])) {
                return Optional.empty();
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(partes[1]);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, new TypeReference<>() {
            });
            Number expiracao = (Number) payload.get("exp");

            if (expiracao == null || Instant.now().getEpochSecond() > expiracao.longValue()) {
                return Optional.empty();
            }

            Number usuarioId = (Number) payload.get("uid");
            String email = (String) payload.get("sub");

            if (usuarioId == null || email == null) {
                return Optional.empty();
            }

            return Optional.of(new JwtUsuario(usuarioId.longValue(), email));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private String assinar(String conteudo) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return base64Url(mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8)));
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record JwtUsuario(Long usuarioId, String email) {
    }
}
