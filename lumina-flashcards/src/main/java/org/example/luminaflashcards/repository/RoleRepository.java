package org.example.luminaflashcards.repository;

import org.example.luminaflashcards.model.ERole;
import org.example.luminaflashcards.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // JpaRepository<Role, Integer> significa:
    // - Role: A entidade que este repositório gerencia.
    // - Integer: O tipo da chave primária da entidade Role (o campo 'id').

    // Encontra uma role pelo seu nome (que é do tipo ERole)
    Optional<Role> findByName(ERole name);
}
