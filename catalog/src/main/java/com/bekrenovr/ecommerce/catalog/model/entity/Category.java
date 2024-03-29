package com.bekrenovr.ecommerce.catalog.model.entity;

import com.bekrenovr.ecommerce.catalog.model.enums.CategoryEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Category {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "enum_value")
    @Enumerated(EnumType.STRING)
    private CategoryEnum enumValue;

    @Column(name = "img_name")
    private String imageName;

    @OneToMany(mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private List<Subcategory> subcategories;
}
