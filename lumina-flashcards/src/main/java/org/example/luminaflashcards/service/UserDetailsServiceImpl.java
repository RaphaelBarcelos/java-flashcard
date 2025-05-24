package org.example.luminaflashcards.service;

import org.example.luminaflashcards.model.User; // Entidade User
import org.example.luminaflashcards.repository.UserRepository; //UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Opcional, para métodos que acessam o BD

import java.util.List;
import java.util.stream.Collectors;

@Service // Informa ao Spring que esta classe é um componente de serviço e deve ser gerenciada por ele (tornando-a um bean).
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional // Garante que as operações de busca no banco sejam transacionais
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Busca o usuário no banco de dados pelo 'username'
        // Lembre-se que o campo na sua entidade User.java é 'username',
        // que por sua vez mapeia para 'nome_usuario' no banco de dados.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado: " + username));

        // 2. Converte as roles (permissões) do seu usuário para o formato que o Spring Security entende (GrantedAuthority)
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        // 3. Retorna um objeto UserDetails (a implementação padrão do Spring Security: org.springframework.security.core.userdetails.User)
        // Este objeto contém as informações que o Spring Security precisa para autenticação e autorização.
        return new org.springframework.security.core.userdetails.User(
                user.getusername(),
                user.getpassword(),
                authorities
        );
        // Outros construtores de org.springframework.security.core.userdetails.User permitem passar
        // flags para enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        // se você tiver esses campos na sua entidade User.
        // Por exemplo:
        // return new org.springframework.security.core.userdetails.User(
        //         user.getUsername(),
        //         user.getPassword(),
        //         true, // enabled
        //         true, // accountNonExpired
        //         true, // credentialsNonExpired
        //         true, // accountNonLocked
        //         authorities);
    }
}
