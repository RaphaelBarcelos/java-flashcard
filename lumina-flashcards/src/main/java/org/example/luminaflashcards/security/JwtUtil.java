package org.example.luminaflashcards.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Importe a exceção correta
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component // Marca esta classe como um componente Spring, para que possa ser injetada
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${app.jwt.secret}") // Injeta o valor da propriedade app.jwt.secret
    private String jwtSecret;

    @Value("${app.jwt.expirationMs}") // Injeta o valor da propriedade app.jwt.expirationMs
    private int jwtExpirationMs;

    // Metodo para gerar a chave de assinatura a partir do segredo
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Gera um token JWT a partir de um objeto Authentication (apos login bem-sucedido)
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Define o "assunto" do token (geralmente o username)
                .setIssuedAt(now) // Data de emissão
                .setExpiration(expiryDate) // Data de expiração
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Assina com a chave e algoritmo
                .compact(); // Constrói o token e o serializa para uma string compacta

    }

    // Gera um token JWT diretamente a partir de um username (útil em alguns cenários)
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Extrai o username de um token JWT
    public String getUsernameFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();

    }

    public boolean validateJwtToken(String authToken) {
        try{
            Jwts.parser()
                    .setSigningKey(getSigningKey()) // Define a chave
                    .build() // Constroi o parser
                    .parseClaimsJws(authToken); //Faz o parse do token
            return true;
        } catch (SignatureException e){
            logger.error("Assinatura JWT invalida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT nao suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Argumento JWT invalido ou string de claims vazia: {}", e.getMessage());
        }
        return false;
    }

    // Helper para extrair o token do header "Authorization" de uma requisição HTTP
    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Remove "Bearer " para obter apenas o token
        }
        return null;
    }
}
