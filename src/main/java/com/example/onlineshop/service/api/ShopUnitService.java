package com.example.onlineshop.service.api;

import com.example.onlineshop.entity.ShopUnit;
import com.example.onlineshop.entity.ShopUnitType;
import dto.ShopUnitImportRequest;
import dto.ShopUnitItem;

import java.util.List;

public interface ShopUnitService {

    void insertShopUnit(ShopUnitImportRequest productRequest, ShopUnitType shopUnitType);

    void insertShopUnit(ShopUnit shopUnit);

    void updateShopUnit(ShopUnitImportRequest productRequest, ShopUnitType shopUnitType);

    void deleteAllShopUnits(ShopUnit shopUnit, List<String> ids);

    ShopUnit findShopUnitById(String id);

    ShopUnit findShopUnitByName(String name);

    ShopUnit findFirstCategories();

    List<ShopUnit> findAllShopUnits();

    List<ShopUnit> findAllCategories();

    List<ShopUnit> getAllChildShopUnits(String id);

    List<ShopUnit> getModifiedProducts(String time);

    List<ShopUnitItem> getAllChildShopUnitsWithHierarchy(ShopUnit shopUnit);
}
