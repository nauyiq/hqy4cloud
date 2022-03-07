package com.hqy.coll.service.impl;


import com.hqy.coll.entity.ThrottledIpBlock;
import com.hqy.coll.service.CollPersistService;
import com.hqy.coll.service.ThrottledIpBlockService;
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
 * @project: hqy-parent-all
 * @create 2021-08-10 15:32
 */
@Slf4j
@Service
public class CollPersistServiceImpl extends AbstractRPCService implements CollPersistService {

    @Resource
    private ThrottledIpBlockService throttledIpBlockService;

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
