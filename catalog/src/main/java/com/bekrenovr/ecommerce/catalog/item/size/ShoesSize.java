package com.bekrenovr.ecommerce.catalog.item.size;

public record ShoesSize(int value) implements Size {
    @Override
    public String getStringValue() {
        return String.valueOf(value);
    }

    @Override
    public int getNumericValue() {
        return value;
    }
}
