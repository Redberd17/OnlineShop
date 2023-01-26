package com.example.onlineshop.service.api;

import com.example.onlineshop.entity.ShopUnitType;
import dto.ShopUnitTypeRequest;

public interface ShopUnitTypeService {
    ShopUnitType findShopUnitType(Long id);

    ShopUnitType addShopUnitType(ShopUnitTypeRequest shopUnitTypeRequest);

    ShopUnitType findShopUnitTypeByName(String shopUnitTypeName);


}
