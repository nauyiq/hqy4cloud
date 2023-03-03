package com.hqy.collector.service.impl.remote;

import com.hqy.collector.entity.RPCExceptionRecord;
import com.hqy.collector.entity.RPCFlowRecord;
import com.hqy.collector.service.RPCExceptionRecordService;
import com.hqy.collector.service.RPCFlowRecordService;
import com.hqy.rpc.monitor.thrift.api.ThriftMonitorService;
import com.hqy.rpc.thrift.struct.ThriftRpcExceptionStruct;
import com.hqy.rpc.thrift.struct.ThriftRpcFlowStruct;
import com.hqy.cloud.util.thread.ParentExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hongK
 * @version 1.0
 * @date 2022/7/22 10:59
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThriftMonitorServiceImpl implements ThriftMonitorService {

    private final RPCFlowRecordService rpcFlowRecordService;
    private final RPCExceptionRecordService rpcExceptionRecordService;

    @Override
    public void collectRpcFlow(ThriftRpcFlowStruct struct) {
        collectRpcFlowList(Collections.singletonList(struct));
    }

    @Override
    public void collectRpcFlowList(List<ThriftRpcFlowStruct> structs) {
        if (CollectionUtils.isEmpty(structs)) {
            return;
        }
        List<RPCFlowRecord> rpcFlowRecords = structs.stream().map(RPCFlowRecord::new).collect(Collectors.toList());
        ParentExecutorService.getInstance().execute(() -> rpcFlowRecordService.insertList(rpcFlowRecords));
    }

    @Override
    public void collectRpcException(ThriftRpcExceptionStruct struct) {
        if (struct == null) {
            return;
        }
        rpcExceptionRecordService.insert(new RPCExceptionRecord(struct));
    }
}
