package org.example.luminaflashcards.config;

import org.example.luminaflashcards.security.JwtAuthenticationFilter; // Seu filtro JWT
import org.example.luminaflashcards.service.UserDetailsServiceImpl; // Seu UserDetailsService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Você já tinha este
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Marca esta classe como uma fonte de definicoes de beans
@EnableWebSecurity
@EnableMethodSecurity(
        prePostEnabled = true, // Permite usar @PreAuthorize e @PostAuthorize
        securedEnabled = true, // Permite usar @Secured
        jsr250Enabled = true   // Permite usar @RolesAllowed (menos comum com Spring Security puro)
)

public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // Filtro JWT

    // Opcional: Se você criar um AuthEntryPointJwt para customizar respostas 401
    // @Autowired
    // private AuthEntryPointJwt unauthorizedHandler;

    @Bean // Expõe este metodo como um bean gerenciado pelo Spring
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder é uma implementação de PasswordEncoder
        // que usa o algoritmo de hashing BCrypt, que é forte e recomendado.
        // Ele lida automaticamente com a geração de "salt" para cada senha.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Este provedor de autenticação usa seu UserDetailsService e PasswordEncoder
        // para validar as credenciais do usuário durante o login (username/password).
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // O AuthenticationManager é o principal responsável por processar uma solicitação de autenticação.
        // Ele será usado no seu AuthController para o endpoint de login.
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF (Cross-Site Request Forgery)
        // APIs stateless JWT geralmente não precisam de CSRF.
        // Opcional: Configura um ponto de entrada para autenticação, se quiser customizar o erro 401
        // .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configura a politica de sessao para STATELESS, nao serao criadas sessoes http

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/test-secure/all").permitAll() // Permite acesso a todos os endpoints sob /api/auth/ (login, registro).
                        .requestMatchers("/api/test-secure/**").authenticated() // Outros de test-secure exigem autenticação base
                        .anyRequest().authenticated() // Todas as outras requisições exigem autenticação
                );

        // Adiciona o DaoAuthenticationProvider configurado
        http.authenticationProvider(authenticationProvider());

        // Adiciona seu JwtAuthenticationFilter ANTES do filtro padrão UsernamePasswordAuthenticationFilter.
        // Isso garante que seu filtro JWT processe o token antes que o Spring tente a autenticação por username/password.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
