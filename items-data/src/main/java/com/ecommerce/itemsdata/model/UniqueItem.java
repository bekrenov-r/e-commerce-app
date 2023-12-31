package com.ecommerce.itemsdata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "unique_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UniqueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "size")
    private String size;

    @Column(name = "color_id")
    private String colorId;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "bar_code")
    private String barCode;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "restock_quantity")
    private Integer restockQuantity;

    @Column(name = "reorder_quantity")
    private Integer reorderQuantity;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

}
