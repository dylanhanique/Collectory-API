package fr.dylanhanique.collectoryapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "collectible_items")
public class CollectibleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "item_value")
    private BigDecimal itemValue;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Enumerated(EnumType.STRING)
    private Rarity rarity;

    @ManyToOne
    @JsonBackReference
    private CollectibleCollection collectibleCollection;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    public CollectibleItem(String name, CollectibleCollection collectibleCollection) {
        this.name = name;
        this.collectibleCollection = collectibleCollection;
    }

}
