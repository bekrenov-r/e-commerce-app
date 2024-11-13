package com.bekrenovr.ecommerce.catalog.item.filters;

public enum Color {
    BLACK, WHITE, RED, YELLOW, GREEN, BLUE, VIOLET, GREY, MULTI;
    public static Color ofString(String s){
        return Color.valueOf(s.toUpperCase());
    }
}