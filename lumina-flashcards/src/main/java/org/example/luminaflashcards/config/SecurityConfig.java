package org.example.luminaflashcards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // Marca esta classe como uma fonte de definicoes de beans
public class SecurityConfig {

    @Bean // Expõe este metodo como um bean gerenciado pelo Spring
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder é uma implementação de PasswordEncoder
        // que usa o algoritmo de hashing BCrypt, que é forte e recomendado.
        // Ele lida automaticamente com a geração de "salt" para cada senha.
        return new BCryptPasswordEncoder();

        // Outras configurações de segurança (como SecurityFilterChain)
        // serão adicionadas aqui nos próximos passos.
    }
}
