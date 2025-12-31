package fr.dylanhanique.collectoryapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import fr.dylanhanique.collectoryapi.dto.CreateUserRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    @Size(min = 3)
    private String username;

    @Setter
    @Column(nullable = false, unique = true)
    @NotBlank
    private String email;

    @Setter
    @Column(nullable = false)
    @Size(min = 8)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonBackReference
    private final List<CollectibleCollection> collectibleCollections = new ArrayList<CollectibleCollection>();

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

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
