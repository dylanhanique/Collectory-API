package fr.dylanhanique.collectoryapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "collections")
public class CollectibleCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    private String description;

    @OneToMany(mappedBy = "collectibleCollection", fetch = FetchType.LAZY)
    @JsonManagedReference
    private final List<CollectibleItem> collectibleItems = new ArrayList<CollectibleItem>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private User user;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    public CollectibleCollection(String name, User user) {
        this.name = name;
        this.user = user;
    };

    public CollectibleCollection(String name, String coverImageUrl, String description, User user) {
        this.name = name;
        this.user = user;
        this.coverImageUrl = coverImageUrl;
        this.description = description;
    };
}
