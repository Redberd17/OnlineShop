package com.example.onlineshop.service.impl;

import com.example.onlineshop.entity.Audit;
import com.example.onlineshop.entity.ShopUnit;
import com.example.onlineshop.entity.ShopUnitType;
import com.example.onlineshop.repository.AuditRepository;
import com.example.onlineshop.repository.ShopUnitRepository;
import com.example.onlineshop.service.api.ShopUnitService;
import com.example.onlineshop.service.api.ShopUnitTypeService;
import com.example.onlineshop.utils.DateTimeUtils;
import com.example.onlineshop.utils.OperationEnum;
import com.example.onlineshop.utils.ShopUnitItemUtils;
import com.example.onlineshop.utils.ShopUnitTypeEnum;
import dto.ShopUnitImportRequest;
import dto.ShopUnitItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ShopUnitServiceImpl implements ShopUnitService {

    private ShopUnitTypeService shopUnitTypeService;

    private ShopUnitRepository shopUnitRepository;
    private AuditRepository auditRepository;

    @Autowired
    public ShopUnitServiceImpl(ShopUnitRepository shopUnitRepository, AuditRepository auditRepository,  ShopUnitTypeService shopUnitTypeService) {
        this.shopUnitTypeService = shopUnitTypeService;
        this.shopUnitRepository = shopUnitRepository;
        this.auditRepository = auditRepository;
    }

    @Override
    public void insertShopUnit(ShopUnitImportRequest productRequest, ShopUnitType shopUnitType) {
        if (Objects.nonNull(shopUnitRepository.findByName(productRequest.getShopUnitName()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Object with name: " + productRequest.getShopUnitName() + " already exists");
        }
        ShopUnit shopUnit = ShopUnit.builder()
                .name(productRequest.getShopUnitName())
                .parentId(Objects.isNull(productRequest.getParentId()) ? null : shopUnitRepository.findById(productRequest.getParentId()).getId())
                .price(productRequest.getPrice())
                .shopUnitType(shopUnitType)
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .build();
        shopUnitRepository.save(shopUnit);
        Audit audit = Audit.builder()
                .objectId(shopUnit.getId())
                .newPrice(shopUnit.getPrice())
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .operation(OperationEnum.INSERT.getName())
                .build();
        auditRepository.save(audit);
        updatePrices(shopUnit);
    }

    @Override
    public void insertShopUnit(ShopUnit shopUnit) {
        shopUnitRepository.save(shopUnit);
        Audit audit = Audit.builder()
                .objectId(shopUnit.getId())
                .newPrice(shopUnit.getPrice())
                .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                .operation(OperationEnum.INSERT.getName())
                .build();
        auditRepository.save(audit);
        updatePrices(shopUnit);
    }

    @Override
    public void updateShopUnit(ShopUnitImportRequest productRequest, ShopUnitType shopUnitType) {
        ShopUnit shopUnit = findShopUnitById(productRequest.getId());
        shopUnit.setName(productRequest.getShopUnitName());
        shopUnit.setLastModifiedDate(DateTimeUtils.getCurrentDateUTC());
        shopUnit.setShopUnitType(shopUnitType);
        shopUnit.setParentId(productRequest.getParentId());
        if (!Objects.equals(productRequest.getPrice(), shopUnit.getPrice())) {
            Audit audit = Audit.builder()
                    .objectId(shopUnit.getId())
                    .oldPrice(shopUnit.getPrice())
                    .newPrice(productRequest.getPrice())
                    .lastModifiedDate(DateTimeUtils.getCurrentDateUTC())
                    .operation(OperationEnum.UPDATE.getName())
                    .build();
            auditRepository.save(audit);
        }
        shopUnit.setPrice(productRequest.getPrice());
        shopUnitRepository.save(shopUnit);
        updatePrices(shopUnit);
    }

    @Override
    public ShopUnit findShopUnitById(String id) {
        return shopUnitRepository.findById(id);
    }

    @Override
    public ShopUnit findShopUnitByName(String name) {
        return shopUnitRepository.findByName(name);
    }

    @Override
    public ShopUnit findFirstCategories() {
        return shopUnitRepository.findFirstByParentIdIsNull();
    }

    @Override
    public List<ShopUnit> findAllShopUnits() {
        return shopUnitRepository.findAll();
    }

    @Override
    public List<ShopUnit> findAllCategories() {
        ShopUnitType shopUnitType = shopUnitTypeService.findShopUnitTypeByName(ShopUnitTypeEnum.CATEGORY.getType());
        return shopUnitRepository.findAllByShopUnitTypeOrderByName(shopUnitType);
    }

    @Override
    public void deleteAllShopUnits(ShopUnit shopUnit, List<String> ids) {
        shopUnitRepository.deleteAllByIdIn(ids);
        updatePrices(shopUnit);
    }

    @Override
    public List<ShopUnit> getAllChildShopUnits(String id) {
        return getChildShopUnits(getChildren(id));
    }

    @Override
    public List<ShopUnit> getModifiedProducts(String time) {
        return shopUnitRepository.getAllModifiedProducts(time);
    }

    @Override
    public List<ShopUnitItem> getAllChildShopUnitsWithHierarchy(ShopUnit shopUnit) {
        List<ShopUnit> children = getChildren(shopUnit.getId());
        if (!CollectionUtils.isEmpty(children)) {
            List<ShopUnitItem> childrenItem = new ArrayList<>();
            for (ShopUnit child : children) {
                ShopUnitItem shopUnitItem = new ShopUnitItem(ShopUnitItemUtils.convert(child),
                        getAllChildShopUnitsWithHierarchy(child));
                childrenItem.add(shopUnitItem);
            }
            return childrenItem;
        }
        if (shopUnit.getShopUnitType().getName().equals(ShopUnitTypeEnum.CATEGORY.getType())) {
            return new ArrayList<>();
        }
        return null;
    }

    private void updatePrices(ShopUnit shopUnit) {
        ShopUnit parentCategory = shopUnitRepository.findById(shopUnit.getParentId());
        if (Objects.nonNull(parentCategory)) {
            List<ShopUnit> childShopUnit = getChildren(parentCategory.getId());
            ShopUnitItemUtils.calculateOveragePrice(parentCategory, childShopUnit);
            if (Objects.nonNull(parentCategory.getParentId())) {
                updatePrices(parentCategory);
            }
            shopUnitRepository.save(parentCategory);
        }
    }

    private List<ShopUnit> getChildShopUnits(List<ShopUnit> shopUnits) {
        List<ShopUnit> childShopUnits = new ArrayList<>();
        for (ShopUnit value : shopUnits) {
            childShopUnits.add(value);
            childShopUnits.addAll(getChildShopUnits(getChildren(value.getId())));
        }
        return childShopUnits;
    }

    private List<ShopUnit> getChildren(String id) {
        return shopUnitRepository.findAllByParentIdOrderByName(id);
    }

}
