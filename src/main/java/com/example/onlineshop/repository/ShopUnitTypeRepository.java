package com.example.onlineshop.repository;

import com.example.onlineshop.entity.ShopUnitType;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface ShopUnitTypeRepository extends CrudRepository<ShopUnitType, Long> {

    ShopUnitType findByNameOrderByName(String name);
}
