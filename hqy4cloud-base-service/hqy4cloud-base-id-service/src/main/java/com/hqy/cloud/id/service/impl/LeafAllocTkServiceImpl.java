package com.hqy.cloud.id.service.impl;

import com.hqy.cloud.id.entities.LeafAlloc;
import com.hqy.cloud.id.mapper.LeafAllocMapper;
import com.hqy.cloud.id.service.LeafAllocTkService;
import com.hqy.cloud.tk.PrimaryLessTkMapper;
import com.hqy.cloud.tk.support.PrimaryLessTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 15:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeafAllocTkServiceImpl extends PrimaryLessTkServiceImpl<LeafAlloc> implements LeafAllocTkService {
    private final LeafAllocMapper mapper;

    @Override
    public PrimaryLessTkMapper<LeafAlloc> getTkDao() {
        return mapper;
    }

    @Override
    public List<String> getAllTags() {
        return mapper.getAllTags();
    }

    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(String key) {
        LeafAlloc leafAlloc = queryById(key);
        if (Objects.isNull(leafAlloc)) {
            throw new IllegalArgumentException("Not found leafAlloc by " + key);
        }
        leafAlloc.setMaxId(leafAlloc.getMaxId() + leafAlloc.getStep());
        update(leafAlloc);
        return leafAlloc;
    }

    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        LeafAlloc entities = queryById(leafAlloc.getBizTag());
        if (Objects.isNull(entities)) {
            throw new IllegalArgumentException("Not found leafAlloc by " + leafAlloc.getBizTag());
        }
        entities.setMaxId(entities.getMaxId() + leafAlloc.getStep());
        update(entities);
        return entities;
    }
}
