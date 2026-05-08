package com.checkin.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Componente responsável pela geração e validação de tokens JWT.
 *
 * Utiliza a biblioteca jjwt para assinar e decodificar tokens. A chave secreta
 * e o tempo de expiração são configuráveis através do application.properties.
 */
@Component
public class JwtTokenProvider {

    /**
     * Chave secreta utilizada para assinar os tokens. Deve possuir tamanho
     * compatível com o algoritmo HS256. Está configurada em base64 no
     * application.properties.
     */
    @Value("${security.jwt.secret}")
    private String jwtSecret;

    /**
     * Tempo de expiração do token em milissegundos. Após esse período o
     * token não será mais válido e o usuário precisará se autenticar
     * novamente.
     */
    @Value("${security.jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Gera um token JWT para o usuário autenticado.
     *
     * @param username Email do usuário que será utilizado como subject
     * @return Token JWT assinado
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Factory de tokens JWT, configurada para usar o algoritmo HS256 e a chave secreta
        return Jwts.builder()
                .setSubject(username) // O subject do token é o email do usuário
                .setIssuedAt(now) // Data de emissão do token
                .setExpiration(expiryDate) // Data de expiração do token
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Assina o token com a chave e o algoritmo especificados
                .compact(); // Gera o token JWT em formato compacto (string)
    }

    /**
     * Obtém o nome de usuário (subject) a partir de um token JWT.
     *
     * @param token Token JWT
     * @return Nome de usuário extraído do token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder() // Configura o parser de tokens JWT, definindo a chave de assinatura para validar o token
                .setSigningKey(getSigningKey()) // Define a chave de assinatura para validar o token
                .build() // Constrói o parser
                .parseClaimsJws(token) // Analisa o token JWT e extrai os claims (informações contidas no token)
                .getBody(); // Obtém o corpo dos claims, que contém as informações do token, incluindo o subject (email do usuário)
        return claims.getSubject(); // Retorna o subject do token, que é o email do usuário
    }

    /**
     * Verifica se um token JWT é válido (assinatura correta e não expirado).
     *
     * @param token Token JWT
     * @return true se o token for válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Converte a chave secreta configurada em base64 para um objeto Key.
     *
     * @return Chave criptográfica utilizada na assinatura do token
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}