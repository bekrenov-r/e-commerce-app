package com.bekrenovr.ecommerce.catalog.category.subcategory;

import com.bekrenovr.ecommerce.catalog.category.Category;
import com.bekrenovr.ecommerce.common.model.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "subcategory")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Subcategory extends AbstractEntity {
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Category category;

    public Subcategory(UUID id, String name, Category category) {
        super(id);
        this.name = name;
        this.category = category;
    }
}
