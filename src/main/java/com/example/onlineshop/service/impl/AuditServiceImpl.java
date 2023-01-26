package com.example.onlineshop.service.impl;

import com.example.onlineshop.entity.Audit;
import com.example.onlineshop.repository.AuditRepository;
import com.example.onlineshop.service.api.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    private AuditRepository auditRepository;

    @Autowired
    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public List<Audit> findAllAuditsForShopUnit(String objectId, String startDate, String endDate) {
        return auditRepository.getAudits(objectId, startDate, endDate);
    }

}
