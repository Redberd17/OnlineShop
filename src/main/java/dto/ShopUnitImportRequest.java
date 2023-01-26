package dto;

import lombok.Data;

@Data
public class ShopUnitImportRequest {
    String id;
    String shopUnitName;
    Integer price;
    String parentId;
    String typeName;
}
