package fr.dylanhanique.collectoryapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import fr.dylanhanique.collectoryapi.dto.CreateUserRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String username;

    @Setter
    @Column(nullable = false)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonBackReference
    private final List<Collection> collections = new ArrayList<Collection>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    };

    public User(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    };

    public static User fromDto(CreateUserRequest dto, String encodedPassword) {
        return new User(dto.username(), dto.email(), encodedPassword);
    };
}
