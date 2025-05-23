package org.example.luminaflashcards.repository;

import org.example.luminaflashcards.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Anotação que marca esta interface como um componente Repository do Spring


public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<User, Long> significa:
    // - User: A entidade que este repositório gerencia.
    // - Long: O tipo da chave primária da entidade User (o campo 'id').

    // Métodos de consulta personalizados (o Spring Data JPA os implementará automaticamente):

    // Encontra um usuário pelo seu nome de usuário (username)
    // Retorna um Optional, que pode ou não conter um User (evita NullPointerExceptions)
    Optional<User> findByUsername(String username); // O nome do metodo deve seguir as convenções do Spring Data JPA

    // Verifica se um usuário existe pelo nome de usuário
    Boolean existsByUsername(String username);

    // Verifica se um usuário existe pelo email (adicionamos email à entidade User)
    Boolean existsByEmail(String email);

}
