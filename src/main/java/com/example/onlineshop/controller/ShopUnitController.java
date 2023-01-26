package com.example.onlineshop.controller;

import com.example.onlineshop.entity.Audit;
import com.example.onlineshop.entity.ShopUnit;
import com.example.onlineshop.entity.ShopUnitType;
import com.example.onlineshop.service.api.AuditService;
import com.example.onlineshop.service.api.ShopUnitService;
import com.example.onlineshop.service.api.ShopUnitTypeService;
import com.example.onlineshop.utils.DateTimeUtils;
import com.example.onlineshop.utils.ShopUnitItemUtils;
import com.example.onlineshop.utils.ShopUnitTypeEnum;
import dto.ShopUnitImportRequest;
import dto.ShopUnitItem;
import dto.ShopUnitTypeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class ShopUnitController {
    private final Logger logger = LoggerFactory.getLogger(ShopUnitController.class);

    private final AuditService auditService;
    private final ShopUnitService shopUnitService;
    private final ShopUnitTypeService shopUnitTypeService;

    @Autowired
    public ShopUnitController(AuditService auditService, ShopUnitService shopUnitService, ShopUnitTypeService shopUnitTypeService) {
        this.auditService = auditService;
        this.shopUnitService = shopUnitService;
        this.shopUnitTypeService = shopUnitTypeService;
    }

    @PostMapping("/imports")
    public ResponseEntity<String> importShopUnit(@RequestBody ShopUnitImportRequest productRequest) {
        if (Objects.isNull(productRequest.getTypeName())) {
            return new ResponseEntity<>("typeName must be provided", HttpStatus.BAD_REQUEST);
        }

        ShopUnitType shopUnitType = shopUnitTypeService.findShopUnitTypeByName(productRequest.getTypeName());

        if (Objects.isNull(productRequest.getShopUnitName()) || productRequest.getShopUnitName().isEmpty()) {
            return new ResponseEntity<>("ShopUnit must have name", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(shopUnitType)) {
            return new ResponseEntity<>("No such shopUnitType", HttpStatus.BAD_REQUEST);
        }

        if (ShopUnitTypeEnum.PRODUCT.getType().equals(shopUnitType.getName())) {
            if (Objects.isNull(productRequest.getPrice())) {
                return new ResponseEntity<>("Product must have price", HttpStatus.BAD_REQUEST);
            }
        } else if (ShopUnitTypeEnum.CATEGORY.getType().equals(shopUnitType.getName())) {
            if (Objects.nonNull(productRequest.getPrice())) {
//            if (!productRequest.getPrice().isEmpty()) {
                return new ResponseEntity<>("Category must not have price", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("ShopUnitType is incorrect", HttpStatus.BAD_REQUEST);
        }

        if (Objects.nonNull(productRequest.getParentId())) {
            ShopUnit parentShopUnit = shopUnitService.findShopUnitById(productRequest.getParentId());
            if (Objects.isNull(parentShopUnit) || ShopUnitTypeEnum.PRODUCT.getType().equals(parentShopUnit.getShopUnitType().getName())) {
                return new ResponseEntity<>("Only category can be as parent", HttpStatus.BAD_REQUEST);
            }
        }
//        try {
//            Integer.parseInt(productRequest.getPrice());
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "parseInt");
//        }

        if (Objects.isNull(productRequest.getId())) {
            shopUnitService.insertShopUnit(productRequest, shopUnitType);
        } else {
            shopUnitService.updateShopUnit(productRequest, shopUnitType);
        }

        logger.info("UPDATE MODEL");
        return new ResponseEntity<>("Insert or update was successful", HttpStatus.OK);
    }

    @PostMapping("/shopUnitType")
    public ResponseEntity<String> importShopUnitType(@RequestBody ShopUnitTypeRequest shopUnitType) {
        if (Objects.isNull(shopUnitType)) {
            return new ResponseEntity<>("shopUnitTypeName must be provided", HttpStatus.BAD_REQUEST);
        }

        ShopUnitType existingShopUnitType = shopUnitTypeService.findShopUnitTypeByName(shopUnitType.getShopUnitTypeName());
        if (Objects.nonNull(existingShopUnitType)) {
            return new ResponseEntity<>(String.format("ShopUnitType \"%s\" is already exist", shopUnitType.getShopUnitTypeName()), HttpStatus.BAD_REQUEST);
        }

        shopUnitTypeService.addShopUnitType(shopUnitType);
        return new ResponseEntity<>(String.format("ShopUnitType \"%s\" was added", shopUnitType.getShopUnitTypeName()), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteShopUnit(@PathVariable String id) {
        if (Objects.isNull(id)) {
            return new ResponseEntity<>("Validation Failed", HttpStatus.BAD_REQUEST);
        }
        ShopUnit shopUnit = shopUnitService.findShopUnitById(id);
        if (Objects.isNull(shopUnit)) {
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);
        }
        List<ShopUnit> children = shopUnitService.getAllChildShopUnits(id);
        children.add(shopUnit);
        shopUnitService.deleteAllShopUnits(shopUnit, children.stream().map(ShopUnit::getId).collect(Collectors.toList()));
        return new ResponseEntity<>("Delete was successful", HttpStatus.OK);
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<ShopUnitItem> getChildShopUnits(@PathVariable String id) {
        ShopUnit currentShopUnit = shopUnitService.findShopUnitById(id);
        if (Objects.isNull(currentShopUnit)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        ShopUnitItem currentShopUnitItem = ShopUnitItemUtils.convert(currentShopUnit);

        List<ShopUnitItem> childShopUnits = shopUnitService.getAllChildShopUnitsWithHierarchy(currentShopUnit);
        currentShopUnitItem.setChildren(childShopUnits);

        logger.info("GET MODEL");
        return new ResponseEntity<>(currentShopUnitItem, HttpStatus.OK);
    }

    @GetMapping("/firstCategories")
    public ResponseEntity<ShopUnitItem> getCategories() {
        ShopUnit shopUnit = shopUnitService.findFirstCategories();
        if (Objects.isNull(shopUnit)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        List<ShopUnitItem> childShopUnits = shopUnitService.getAllChildShopUnitsWithHierarchy(shopUnit);
        ShopUnitItem shopUnitItem = ShopUnitItemUtils.convert(shopUnit);
        shopUnitItem.setChildren(childShopUnits);

        return new ResponseEntity<>(shopUnitItem, HttpStatus.OK);
    }

    @GetMapping("/sales")
    public ResponseEntity<List<ShopUnit>> getModifiedProducts(@RequestParam String time) {
        DateTimeUtils dateValidator = new DateTimeUtils(DateTimeFormatter.ISO_DATE_TIME);
        if (!dateValidator.isValid(time)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation Failed");
        }
        List<ShopUnit> modifiedProducts = shopUnitService.getModifiedProducts(time);
        if (CollectionUtils.isEmpty(modifiedProducts)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(modifiedProducts, HttpStatus.OK);
    }

    @GetMapping("/node/{id}/statistic")
    public ResponseEntity<List<Audit>> getStatistic(@PathVariable String id, @RequestParam String dateStart, @RequestParam String dateEnd) {
        DateTimeUtils dateValidator = new DateTimeUtils(DateTimeFormatter.ISO_DATE_TIME);
        if (!dateValidator.isValid(dateStart) || !dateValidator.isValid(dateEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation Failed");
        }
        ShopUnit shopUnit = shopUnitService.findShopUnitById(id);
        if (Objects.isNull(shopUnit)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        List<Audit> audits = auditService.findAllAuditsForShopUnit(id, dateStart, dateEnd);
        return new ResponseEntity<>(audits, HttpStatus.OK);
    }

    @GetMapping("/allCategories")
    public ResponseEntity<List<ShopUnit>> getAllCategories() {
        List<ShopUnit> categories = shopUnitService.findAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/allShopUnits")
    public ResponseEntity<List<ShopUnit>> getAllShopUnits() {
        List<ShopUnit> shopUnits = shopUnitService.findAllShopUnits();
        return new ResponseEntity<>(shopUnits, HttpStatus.OK);
    }

    @GetMapping("/generateTestData")
    public ResponseEntity<String> generateTestData() {
        shopUnitTypeService.addShopUnitType(new ShopUnitTypeRequest("Category"));
        shopUnitTypeService.addShopUnitType(new ShopUnitTypeRequest("Product"));
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("Shop")
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Category"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("Technics")
                .parentId(shopUnitService.findShopUnitByName("Shop").getId())
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Category"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("Phones")
                .parentId(shopUnitService.findShopUnitByName("Technics").getId())
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Category"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("Tablets")
                .parentId(shopUnitService.findShopUnitByName("Technics").getId())
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Category"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("Headphones")
                .parentId(shopUnitService.findShopUnitByName("Technics").getId())
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Category"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("Apple")
                .parentId(shopUnitService.findShopUnitByName("Phones").getId())
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Category"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("Samsung")
                .parentId(shopUnitService.findShopUnitByName("Phones").getId())
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Category"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("iPhone X")
                .parentId(shopUnitService.findShopUnitByName("Apple").getId())
                .price(60000)
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Product"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("iPhone Xs max")
                .parentId(shopUnitService.findShopUnitByName("Apple").getId())
                .price(50000)
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Product"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("Galaxy s20 5g")
                .parentId(shopUnitService.findShopUnitByName("Samsung").getId())
                .price(35000)
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Product"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("iPad 4")
                .parentId(shopUnitService.findShopUnitByName("Tablets").getId())
                .price(25000)
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Product"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        shopUnitService.insertShopUnit(ShopUnit.builder()
                .name("Honor 10")
                .parentId(shopUnitService.findShopUnitByName("Phones").getId())
                .price(20000)
                .shopUnitType(shopUnitTypeService.findShopUnitTypeByName("Product"))
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build());
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
