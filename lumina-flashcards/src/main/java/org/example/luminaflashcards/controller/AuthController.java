package org.example.luminaflashcards.controller;

import org.example.luminaflashcards.dto.JwtResponse;
import org.example.luminaflashcards.dto.LoginRequest;
import org.example.luminaflashcards.dto.MessageResponse;
import org.example.luminaflashcards.dto.SignupRequest;
import org.example.luminaflashcards.model.ERole; // Seu Enum ERole
import org.example.luminaflashcards.model.Role;  // Sua entidade Role
import org.example.luminaflashcards.model.User;  // Sua entidade User
import org.example.luminaflashcards.repository.RoleRepository;
import org.example.luminaflashcards.repository.UserRepository;
import org.example.luminaflashcards.security.JwtUtil;
import jakarta.validation.Valid; // Para ativar a validação dos DTOs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600) // Permite requisicoes de qualquer origem (util para desenvolvimento com frontend)
@RestController // Combina @Controller e @ResponseBody, indicando que os retornos dos metodos serao o corpo da resposta HTTP
@RequestMapping("/api/auth") //Mapeia todas as requisicoes que comecam com /api/auth para este controller
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager; // Para processar tentativas de autenticação

    @Autowired
    UserRepository userRepository; // Para interagir com os dados dos usuarios

    @Autowired
    RoleRepository roleRepository; //Para interagir com os dados das roles

    @Autowired
    PasswordEncoder passwordEncoder; // Para codificar senhas

    @Autowired
    JwtUtil jwtUtil; // Para gerar tokens JWT

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. Autentica o usuario com username e password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Se a autenticação for bem-sucedida, define a autenticação no SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Gera um token JWT
        String jwt = jwtUtil.generateJwtToken(authentication);

        // 4. Obtém os detalhes do usuário (UserDetails) a partir do objeto Authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 5. Obtèm o User entity para pegar ID e email (já que UserDetails não os tem por padrão)
        User userEntity = userRepository.findByUsername(userDetails.getUsername())
                            .orElseThrow(() -> new RuntimeException("Erro ao buscar usuario após login"));

        // 6. Colega os nomes das roles (authorities)
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 7. Retorna o token JWT e informações do usuario na resposta
        return ResponseEntity.ok(new JwtResponse(jwt,
                userEntity.getId(),
                userDetails.getUsername(),
                userEntity.getEmail(), //Adicionado email
                roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        // 1. Verifica se o username ja existe
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erro: Nome de usuario ja esta cadastrado"));
        }
        // 2. Verifica se o email ja existe
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erro: Email ja esta cadastrado"));
        }
        // 3. Cria uma nova instancia de User
        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()) // SENHA CRIPTOGRAFADA
        );
        // 4. Processa as roles fornecidas na requisição
        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null || strRoles.isEmpty()) {
            // Se nenhuma role for específicada, atribui ROLE_USER por padrão
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Erro ao buscar role"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erro ao buscar role"));
                        roles.add(adminRole);
                        break;
                    case "mod":
                    case "moderator":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Erro ao buscar role"));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Erro ao buscar role"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles); // Defina as roles no objeto user

        // 5. Salva o usuario no bd
        userRepository.save(user);

        // 6. Retorna uma mensagem de sucesso
        return ResponseEntity.ok(new MessageResponse("Usuario registrado com sucesso"));
    }
}
