package com.example.onlineshop.service.api;

import com.example.onlineshop.entity.Audit;

import java.util.List;

public interface AuditService {

    List<Audit> findAllAuditsForShopUnit(String objectId, String startDate, String endDate);
}
