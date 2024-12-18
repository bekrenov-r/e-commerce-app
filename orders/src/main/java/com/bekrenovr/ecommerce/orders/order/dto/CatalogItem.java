package com.bekrenovr.ecommerce.orders.order.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record CatalogItem(
        UUID id,
        String name,
        double discount,
        double price,
        double priceAfterDiscount,
        List<UniqueItemResponse> uniqueItems
) { }
