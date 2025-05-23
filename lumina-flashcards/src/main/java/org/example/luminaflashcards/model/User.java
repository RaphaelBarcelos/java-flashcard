package org.example.luminaflashcards.model;

import jakarta.persistence.*; // Importa as anotações da JPA
import org.hibernate.annotations.CreationTimestamp;

import org.example.luminaflashcards.model.Role;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity // Anotação que marca esta classe como uma entidade JPA (será uma tabela no BD)
@Table(name = "usuarios",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "nome_usuario"),
            @UniqueConstraint(columnNames =  "email")
        })
public class User {

    @Id // Marca o campo 'id' como a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura a geração automática do ID (auto-incremento)
    private long id;

    @Column(name = "nome_usuario", nullable = false, unique = true, length = 100) // Define a coluna 'username', não pode ser nula e deve ser única
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255) // Define a coluna 'password', não pode ser nula
    private String password;

    @CreationTimestamp // Hibernate para preencher o campo automaticamente ma criação
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao;

    // Relacionamento Muitos-para-Muitos com a entidade Role (que criaremos em seguida)
    // FetchType.EAGER significa que as roles serão carregadas junto com o usuário.
    // Para aplicações com muitas roles ou cenários complexos, FetchType.LAZY pode ser mais performático.

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Construtor padrão exigido pela JPA
    public User() {

        }
    // Construtor para facilitar a criação de usuários
    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }
    // SETTERS E GETTERS
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getusername() {
        return username;
    }
    public void setusername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getpassword() {
        return password;
    }
    public void setpassword(String password) {
        this.password = password;
    }
    public OffsetDateTime getDataCriacao() {
        return dataCriacao;
    }
    public void setDataCriacao(OffsetDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // (Opcional) Métodos equals() e hashCode() para manipular User em coleções
    // e precisar de uma lógica de igualdade específica.

}
