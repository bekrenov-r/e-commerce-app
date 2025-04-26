package com.bekrenovr.ecommerce.catalog.item;

import com.bekrenovr.ecommerce.catalog.category.CategoryResponse;
import com.bekrenovr.ecommerce.catalog.item.filters.Color;
import com.bekrenovr.ecommerce.catalog.item.filters.Gender;
import com.bekrenovr.ecommerce.catalog.item.filters.Material;
import com.bekrenovr.ecommerce.catalog.item.filters.Season;
import com.bekrenovr.ecommerce.catalog.item.metadata.ItemMetadata;
import com.bekrenovr.ecommerce.catalog.item.uniqueitem.UniqueItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public final class ItemDetailedResponse {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private Double discount;
    private Double priceAfterDiscount;
    private Color color;
    private Gender gender;
    private CategoryResponse category;
    private String itemCode;
    private List<UniqueItemDTO> uniqueItems;
    private String brand;
    private Double rating;
    private Season season;
    private Material material;
    private ItemMetadata metadata;
}
