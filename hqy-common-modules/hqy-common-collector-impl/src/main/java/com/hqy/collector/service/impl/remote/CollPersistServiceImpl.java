package com.hqy.collector.service.impl.remote;


import com.hqy.base.common.result.CommonResultCode;
import com.hqy.collector.entity.ThrottledIpBlock;
import com.hqy.coll.service.CollPersistService;
import com.hqy.collector.service.ThrottledIpBlockService;
import com.hqy.coll.struct.ThrottledIpBlockStruct;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qy
 * @date 2021-08-10 15:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollPersistServiceImpl extends AbstractRPCService implements CollPersistService {

    private final ThrottledIpBlockService throttledIpBlockService;

    @Override
    public void saveThrottledIpBlockHistory(ThrottledIpBlockStruct struct) {
        AssertUtil.notNull(struct, CommonResultCode.INVALID_DATA.message);
        ThrottledIpBlock ipBlock = new ThrottledIpBlock(struct.throttleBy, struct.url, struct.accessJson, struct.blockedSeconds, struct.env, struct.ip);
        boolean insert = throttledIpBlockService.insert(ipBlock);
        if (!insert) {
            log.error("@@@ Insert throttledIpBlock data failure, struct:{}", JsonUtil.toJson(struct));
        }
    }
}
