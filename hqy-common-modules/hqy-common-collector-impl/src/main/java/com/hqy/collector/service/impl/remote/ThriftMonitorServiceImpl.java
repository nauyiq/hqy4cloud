package com.hqy.collector.service.impl.remote;

import com.hqy.collector.service.RPCFlowRecordService;
import com.hqy.rpc.monitor.thrift.api.ThriftMonitorService;
import com.hqy.rpc.thrift.struct.ThriftRpcExceptionStruct;
import com.hqy.rpc.thrift.struct.ThriftRpcFlowStruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/22 10:59
 */
@Service
@RequiredArgsConstructor
public class ThriftMonitorServiceImpl implements ThriftMonitorService {
    private static final Logger log = LoggerFactory.getLogger(ThriftMonitorServiceImpl.class);

    private final RPCFlowRecordService rpcFlowRecordService;

    @Override
    public void collectRpcFlow(ThriftRpcFlowStruct struct) {
        log.info("1");
    }

    @Override
    public void collectRpcFlowList(List<ThriftRpcFlowStruct> structs) {
        log.info("2");
    }

    @Override
    public void collectRpcException(ThriftRpcExceptionStruct struct) {
        log.info("1");
    }
}
