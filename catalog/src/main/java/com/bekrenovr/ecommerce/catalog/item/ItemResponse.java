package com.bekrenovr.ecommerce.catalog.item;

import com.bekrenovr.ecommerce.catalog.item.filters.Color;
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
public class ItemResponse {
    private UUID id;
    private String name;
    private Double price;
    private Double discount;
    private Double priceAfterDiscount;
    private String brand;
    private Double rating;
    private Color color;
    private Season season;
    private Material material;
    private List<UniqueItemDTO> uniqueItems;
    private ItemMetadata metadata;
}
