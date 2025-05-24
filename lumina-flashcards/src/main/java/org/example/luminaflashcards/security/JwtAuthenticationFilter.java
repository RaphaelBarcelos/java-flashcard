package org.example.luminaflashcards.security;

import org.example.luminaflashcards.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils; // StringUtils e util, mas o parseJwt já usa.
import org.springframework.web.filter.OncePerRequestFilter; // Importante estender esta classe

import java.io.IOException;

@Component // Marca esta classe como um componente Spring, para que seja detectada e gerenciada
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter garante que o filtro seja executado apenas uma vez por requisição.

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil; // Injeta seu utilitario JWT

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Injeta seu serviço de detalhes do usuário

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Tenta extrair o token JWT da requisição
            String jwt = jwtUtil.parseJwt(request); // Usa o metodo que criamos no JwtUtil

            // 2. Se um token foi encontrado e é válido...
            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                // 3. Extrai o username do token
                String username = jwtUtil.getUsernameFromJwtToken(jwt);

                // 4. Carrega os detalhes do usuário (UserDetails) a partir do username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Cria um objeto de autenticação do Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                // 6. Define detalhes adicionais da autenticação (como endereço IP, etc.)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Define o usuario como autenticado no Contexto de Segurança do Spring
                // A partir deste ponto, o Spring Security considera o usuario atual como autenticado.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Se qualquer erro ocorrer durante o processamento do token, logamos o erro.
            // A requisicaoo continuara, mas sem um usuario autenticado (a menos que outro filtro o faça).
            logger.error("Não foi possível definir a autenticação do usuário: {}", e.getMessage());
        }
        // 8. CRUCIAL: Continua a cadeia de filtros
        // Independentemente de o token ser válido ou não, ou de ter ocorrido um erro,
        // a requisicao deve prosseguir para os proximos filtros e, eventualmente, para o servlet/controller.
        filterChain.doFilter(request, response);
    }
}
