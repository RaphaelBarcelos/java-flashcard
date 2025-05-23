package org.example.luminaflashcards.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles") // Nome da tabela no banco de dados para as roles
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Pode ser Long também

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private ERole name;  // O nome da role, usando o Enum

    // Construtor padrão
    public Role() {
    }

    // Construtor para facilitar a criação
    public Role(ERole name) {
        this.name = name;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}