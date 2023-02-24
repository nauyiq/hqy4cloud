package com.hqy.collector.service.impl.remote;


import com.github.pagehelper.PageInfo;
import com.hqy.coll.service.CollPersistService;
import com.hqy.coll.struct.PageThrottledBlockResultStruct;
import com.hqy.coll.struct.ThrottledBlockStruct;
import com.hqy.collector.converter.CollectorServiceConverter;
import com.hqy.collector.entity.ThrottledBlock;
import com.hqy.collector.service.ThrottledBlockService;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import com.hqy.rpc.thrift.struct.PageStruct;
import com.hqy.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qy
 * @date 2021-08-10 15:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollPersistServiceImpl extends AbstractRPCService implements CollPersistService {

    private final ThrottledBlockService throttledBlockService;

    @Override
    public void saveThrottledBlockHistory(ThrottledBlockStruct struct) {
        if (Objects.isNull(struct)) {
            log.warn("Throttled Block Struct should not be null.");
            return;
        }
        ThrottledBlock ipBlock = new ThrottledBlock(struct);
        if (!throttledBlockService.insert(ipBlock)) {
            log.error("@@@ Insert throttledIpBlock data failure, struct:{}", JsonUtil.toJson(struct));
        }
    }

    @Override
    public void deleteThrottledBlockHistory(Long id) {
        if (id == null) {
            log.warn("Failed execute to delete Throttled block history, because id is null.");
            return;
        }

        if (!throttledBlockService.deleteByPrimaryKey(id)) {
            log.warn("Failed execute to delete Throttled block history.");
        }
    }

    @Override
    public PageThrottledBlockResultStruct getPageThrottledBlock(String throttleBy, String ip, String uri, PageStruct struct) {
        PageInfo<ThrottledBlock> pageInfo = throttledBlockService.queryPage(throttleBy, ip, uri, struct);
        List<ThrottledBlock> throttledBlocks = pageInfo.getList();
        PageThrottledBlockResultStruct pageThrottledBlockResultStruct;
        if (CollectionUtils.isEmpty(throttledBlocks)) {
            pageThrottledBlockResultStruct = new PageThrottledBlockResultStruct();
        } else {
            List<ThrottledBlockStruct> blockStructs = throttledBlocks.stream().map(CollectorServiceConverter.CONVERTER::convert).collect(Collectors.toList());
            pageThrottledBlockResultStruct = new PageThrottledBlockResultStruct(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), blockStructs);
        }
        return pageThrottledBlockResultStruct;
    }



}
