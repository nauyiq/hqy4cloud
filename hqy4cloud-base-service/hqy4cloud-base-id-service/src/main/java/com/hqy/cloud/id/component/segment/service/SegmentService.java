package com.hqy.cloud.id.component.segment.service;

import com.hqy.cloud.id.exception.InitException;
import com.hqy.cloud.id.service.IdGenService;
import com.hqy.cloud.id.struct.ResultStruct;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * SegmentService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 16:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentService implements IdGenService {
    private final LeafSegmentIdGen idGen;

    @SneakyThrows
    @PostConstruct
    public void init() {
        if (this.idGen.init()) {
            log.info("Segment Service Init Successfully.");
        } else {
            throw new InitException("Segment Service Init Fail");
        }
    }

    @Override
    public ResultStruct get(String key) {
        return this.idGen.get(key);
    }
}
