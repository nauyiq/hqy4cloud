package com.hqy.coll.gateway.service;


import com.hqy.coll.gateway.struct.ThrottledIpBlockStruct;
import com.hqy.rpc.api.AbstractRPCService;
import org.springframework.stereotype.Service;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-10 15:32
 */
@Service
public class CollPersistServiceImpl extends AbstractRPCService implements CollPersistService {


    @Override
    public void saveThrottledIpBlockHistory(ThrottledIpBlockStruct struct) {

    }
}
