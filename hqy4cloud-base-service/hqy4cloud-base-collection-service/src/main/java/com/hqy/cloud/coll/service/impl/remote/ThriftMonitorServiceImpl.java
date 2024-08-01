package com.hqy.cloud.coll.service.impl.remote;

import com.hqy.cloud.coll.entity.RPCExceptionRecord;
import com.hqy.cloud.coll.entity.RPCFlowRecord;
import com.hqy.cloud.coll.service.RPCExceptionRecordService;
import com.hqy.cloud.coll.service.RPCFlowRecordService;
import com.hqy.cloud.rpc.monitor.thrift.service.ThriftMonitorService;
import com.hqy.cloud.rpc.thrift.struct.ThriftRpcExceptionStruct;
import com.hqy.cloud.rpc.thrift.struct.ThriftRpcFlowStruct;
import com.hqy.cloud.util.ProjectExecutors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
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
        ProjectExecutors.getInstance().execute(() -> rpcFlowRecordService.insertList(rpcFlowRecords));
    }

    @Override
    public void collectRpcException(ThriftRpcExceptionStruct struct) {
        if (struct == null) {
            return;
        }
        rpcExceptionRecordService.insert(new RPCExceptionRecord(struct));
    }
}
