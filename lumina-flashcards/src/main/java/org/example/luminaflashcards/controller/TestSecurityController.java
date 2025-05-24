package org.example.luminaflashcards.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600) // Permite CORS para testes
@RestController
@RequestMapping("/api/test-secure") // Este controller responderá por /api/test-secure
public class TestSecurityController {

    @GetMapping("/all") // Caminho completo: /api/test-secure/all
    // SEM @PreAuthorize ou @Secured aqui, pois queremos que seja público via SecurityConfig
    public ResponseEntity<String> getPublicData() {
        return ResponseEntity.ok("Estes são dados públicos, acessíveis por todos!");
    }

    @GetMapping("/authenticated") // Caminho completo: /api/test-secure/authenticated
    @PreAuthorize("isAuthenticated()") // Só para quem está autenticado (qualquer token JWT válido)
    public ResponseEntity<String> getAuthenticatedData(Principal principal) {
        // Principal contém o username do usuário autenticado
        return ResponseEntity.ok("Olá, " + principal.getName() + "! Se você está vendo isso, você está autenticado.");
    }

    @GetMapping("/user-only") // Caminho completo: /api/test-secure/user-only
    @PreAuthorize("hasRole('USER')") // Só para quem tem a role ROLE_USER
    public ResponseEntity<String> getUserData(Principal principal) {
        return ResponseEntity.ok("Olá, " + principal.getName() + "! Estes são dados específicos para usuários com ROLE_USER.");
    }

    @GetMapping("/admin-only") // Caminho completo: /api/test-secure/admin-only
    @PreAuthorize("hasRole('ADMIN')") // Só para quem tem a role ROLE_ADMIN
    public ResponseEntity<String> getAdminData(Principal principal) {
        return ResponseEntity.ok("Olá, " + principal.getName() + "! Estes são dados específicos para usuários com ROLE_ADMIN.");
    }
}