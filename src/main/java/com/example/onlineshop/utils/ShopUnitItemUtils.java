package com.example.onlineshop.utils;

import com.example.onlineshop.entity.ShopUnit;
import dto.ShopUnitItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShopUnitItemUtils {

    public static ShopUnitItem convert(ShopUnit shopUnit) {
        return new ShopUnitItem(shopUnit.getId(), shopUnit.getName(),
                shopUnit.getPrice(), shopUnit.getParentId(),
                shopUnit.getLastModifiedDate(), shopUnit.getShopUnitType());
    }

    public static List<ShopUnitItem> convert(List<ShopUnit> shopUnits) {
        List<ShopUnitItem> shopUnitItems = new ArrayList<>();
        for (ShopUnit shopUnit : shopUnits) {
            shopUnitItems.add(convert(shopUnit));
        }
        return shopUnitItems;
    }

    public static void calculateOveragePrice(ShopUnit shopUnitItem, List<ShopUnit> childShopUnitItems) {
        int overagePrice = 0;
        for (ShopUnit ch : childShopUnitItems) {
            overagePrice += Objects.isNull(ch.getPrice()) ? 0 : ch.getPrice();
        }
        if (childShopUnitItems.size() == 0) {
            shopUnitItem.setPrice(null);
        } else {
            shopUnitItem.setPrice(overagePrice / childShopUnitItems.size());
        }
    }
}
