package com.example.onlineshop.repository;

import com.example.onlineshop.entity.ShopUnit;
import com.example.onlineshop.entity.ShopUnitType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface ShopUnitRepository extends CrudRepository<ShopUnit, Long> {

    ShopUnit findById(String id);

    ShopUnit findByName(String name);

    void deleteAllByIdIn(List<String> ids);

    List<ShopUnit> findAllByParentIdOrderByName(String id);

    ShopUnit findFirstByParentIdIsNull();

    List<ShopUnit> findAll();

    List<ShopUnit> findAllByShopUnitTypeOrderByName(ShopUnitType shopUnitType);

    @Query(value = "SELECT * from products prod where type_id = (select id from shop_unit_type type where type.name = 'Product')\n" +
            "  and TO_TIMESTAMP(prod.last_modified_date, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"')\n" +
            "      between (TO_TIMESTAMP(?1, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') - INTERVAL '1 DAY')\n" +
            "                   and (TO_TIMESTAMP(?1, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"')) order by prod.last_modified_date desc", nativeQuery = true)
    List<ShopUnit> getAllModifiedProducts(String time);

}
