package fr.dylanhanique.collectoryapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    private String imageUrl;

    private BigDecimal value;

    private Integer year;

    @Enumerated(EnumType.STRING)
    private Rarity rarity;

    @ManyToOne
    @JsonBackReference
    private Collection collection;

    public CollectibleItem(String name, Collection collection) {
        this.name = name;
        this.collection = collection;
    }

}
