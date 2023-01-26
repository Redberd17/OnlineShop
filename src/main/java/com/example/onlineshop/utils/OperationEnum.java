package com.example.onlineshop.utils;

import lombok.Getter;

@Getter
public enum OperationEnum {
    INSERT("Insert new object"),

    UPDATE("Update price");

    private String name;

    OperationEnum(String name) {
        this.name = name;
    }
}
