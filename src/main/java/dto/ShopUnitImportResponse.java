package dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ShopUnitImportResponse {
    String id;
    String shopUnitName;
    Integer price;
    String parentId;
    OffsetDateTime lastModifiedDate;
}
