package com.hqy.coll.service.impl;


import com.hqy.coll.entity.RPCMinuteFlowRecord;
import com.hqy.coll.entity.ThrottledIpBlock;
import com.hqy.coll.service.CollPersistService;
import com.hqy.coll.service.RPCMinuteFlowRecordService;
import com.hqy.coll.service.ThrottledIpBlockService;
import com.hqy.coll.struct.RPCMinuteFlowRecordStruct;
import com.hqy.coll.struct.ThrottledIpBlockStruct;
import com.hqy.fundation.common.result.CommonResultCode;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qy
 * @date 2021-08-10 15:32
 */
@Slf4j
@Service
public class CollPersistServiceImpl extends AbstractRPCService implements CollPersistService {

    @Resource
    private ThrottledIpBlockService throttledIpBlockService;

    @Resource
    private RPCMinuteFlowRecordService rpcMinuteFlowRecordService;

    @Override
    public void saveThrottledIpBlockHistory(ThrottledIpBlockStruct struct) {
        AssertUtil.notNull(struct, CommonResultCode.INVALID_DATA.message);
        ThrottledIpBlock ipBlock = new ThrottledIpBlock(struct.throttleBy, struct.url, struct.accessJson, struct.blockedSeconds, struct.env, struct.ip);
        boolean insert = throttledIpBlockService.insert(ipBlock);
        if (!insert) {
            log.error("@@@ Insert throttledIpBlock data failure, struct:{}", JsonUtil.toJson(struct));
        }
    }

    @Override
    public void saveRpcMinuteFlowRecord(RPCMinuteFlowRecordStruct struct) {
        AssertUtil.notNull(struct, CommonResultCode.INVALID_DATA.message);
        RPCMinuteFlowRecord flowRecord = new RPCMinuteFlowRecord(struct);
        boolean insert = rpcMinuteFlowRecordService.insert(flowRecord);
        if (!insert) {
            log.error("@@@ Insert RPCMinuteFlowRecord data failure, struct:{}", JsonUtil.toJson(struct));
        }
    }
}
