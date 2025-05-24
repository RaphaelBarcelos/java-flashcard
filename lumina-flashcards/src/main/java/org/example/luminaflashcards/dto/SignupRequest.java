package org.example.luminaflashcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set; // Recebe um conjunto de strings de roles

public class SignupRequest {

    @NotBlank(message = "O nome de usuario nao pode estar em branco")
    @Size(min = 3, max = 20, message = "O nome de usuario deve ter entre 3 e 20 caracteres")
    private String username;

    @NotBlank(message = "O email nao pode estar em branco")
    @Size(max = 50, message = "O email nao pode ter mais de 50 caracteres")
    private String email;

    @NotBlank(message = "A senha nao pode estar em branco")
    @Size(min = 6, max = 40, message = "A senha deve ter entre 6 e 40 caracteres")
    private String password;

    private Set<String> role; // Ex: ["user"], ["admin", "moderator"]. Sera processado no controller.

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }
}
