package com.example.onlineshop.utils;

import lombok.Getter;

@Getter
public enum ShopUnitTypeEnum {
    PRODUCT("Product"),

    CATEGORY("Category");

    private String type;

    ShopUnitTypeEnum(String type) {
        this.type = type;
    }
}
