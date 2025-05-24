package org.example.luminaflashcards.controller;

import org.example.luminaflashcards.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importe esta anotação
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal; // Para obter informações do usuário autenticado

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    //Endpoint publico (exemplo)
    @GetMapping("/public")
    public ResponseEntity<String> getPublicFlashcardsInfo() {
        return ResponseEntity.ok("Essa e uma informacao publica sobre flashcards");
    }

    //Endpoint para qualquer usuario autenticado
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()") // So pode ser acessado se o usuario estiver autenticado
    public ResponseEntity<String> getMyFlashcardsInfo(Principal principal) {
        //O objeto principal contem o nome do usuairo autenticado
        return ResponseEntity.ok("Ola, " + principal.getName() + "! Aqui estao seus flashcards");
    }
    //Endpoint apenas para usuarios com a role user
    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<String> getSpecificFlashcardForUser(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok("Usuario " + principal.getName() + " (ROLE_USER) acessando flashcard com id" + id);
    }

    // Endpoint apenas para usuários com a role admin
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')") // Só pode ser acessado por usuários com ROLE_ADMIN
    public ResponseEntity<String> getAllFlashcardsForAdmin(Principal principal) {
        return ResponseEntity.ok("Admin " + principal.getName() + " vendo todos os flashcards. (Implementação pendente)");
    }

    // Endpoint para usuários com role 'USER' OU 'ADMIN'
    @GetMapping("/shared/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> getSharedFlashcard(@PathVariable Long id) {
        return ResponseEntity.ok("Flashcard compartilhado com ID: " + id + " acessado. (Implementação pendente)");
    }
}
