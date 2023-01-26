package dto;

import com.example.onlineshop.entity.ShopUnitType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ShopUnitItem {
    String id;
    String shopUnitName;
    Integer price;
    String parentId;
    String lastModifiedDate;
    ShopUnitType shopUnitType;
    List<ShopUnitItem> children;


    public ShopUnitItem(String id, String shopUnitName,
                        Integer price, String parentId,
                        String lastModifiedDate, ShopUnitType shopUnitType) {
        this.id = id;
        this.shopUnitName = shopUnitName;
        this.price = price;
        this.parentId = parentId;
        this.lastModifiedDate = lastModifiedDate;
        this.shopUnitType = shopUnitType;
    }

    public ShopUnitItem(ShopUnitItem shopUnitItem, List<ShopUnitItem> children) {
        this.id = shopUnitItem.id;
        this.shopUnitName = shopUnitItem.shopUnitName;
        this.price = shopUnitItem.price;
        this.parentId = shopUnitItem.parentId;
        this.lastModifiedDate = shopUnitItem.lastModifiedDate;
        this.shopUnitType = shopUnitItem.shopUnitType;
        this.children = children;
    }
}
