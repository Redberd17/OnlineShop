package com.example.onlineshop.service.impl;

import com.example.onlineshop.entity.ShopUnitType;
import com.example.onlineshop.repository.ShopUnitTypeRepository;
import com.example.onlineshop.service.api.ShopUnitTypeService;
import dto.ShopUnitTypeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopUnitTypeServiceImpl implements ShopUnitTypeService {

    private ShopUnitTypeRepository shopUnitTypeRepository;

    @Autowired
    public ShopUnitTypeServiceImpl(ShopUnitTypeRepository shopUnitTypeRepository) {
        this.shopUnitTypeRepository = shopUnitTypeRepository;
    }

    @Override
    public ShopUnitType findShopUnitType(Long id) {
        return shopUnitTypeRepository.findById(id).orElse(null);
    }

    @Override
    public ShopUnitType addShopUnitType(ShopUnitTypeRequest shopUnitTypeRequest) {
        ShopUnitType shopUnitType = ShopUnitType.builder()
                .name(shopUnitTypeRequest.getShopUnitTypeName())
                .build();
        return shopUnitTypeRepository.save(shopUnitType);
    }

    @Override
    public ShopUnitType findShopUnitTypeByName(String shopUnitTypeName) {
        return shopUnitTypeRepository.findByNameOrderByName(shopUnitTypeName);
    }
}

