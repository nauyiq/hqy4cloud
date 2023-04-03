package com.hqy.cloud.coll.service.impl.remote;

import com.github.pagehelper.PageInfo;
import com.hqy.cloud.coll.service.RpcLogRemoteService;
import com.hqy.cloud.coll.struct.PageRpcExceptionRecordStruct;
import com.hqy.cloud.coll.struct.PageRpcFlowRecordStruct;
import com.hqy.cloud.coll.struct.RpcExceptionRecordStruct;
import com.hqy.cloud.coll.struct.RpcFlowRecordStruct;
import com.hqy.cloud.coll.converter.CollectorServiceConverter;
import com.hqy.cloud.coll.entity.RPCExceptionRecord;
import com.hqy.cloud.coll.entity.RPCFlowRecord;
import com.hqy.cloud.coll.service.RPCExceptionRecordService;
import com.hqy.cloud.coll.service.RPCFlowRecordService;
import com.hqy.cloud.rpc.thrift.service.AbstractRPCService;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:42
 */
@Service
@RequiredArgsConstructor
public class RpcLogRemoteServiceImpl extends AbstractRPCService implements RpcLogRemoteService {

    private final RPCExceptionRecordService exceptionRecordService;
    private final RPCFlowRecordService flowRecordService;

    @Override
    public PageRpcFlowRecordStruct pageRpcFlowLog(String caller, String provider, PageStruct pageStruct) {
        PageInfo<RPCFlowRecord> pageInfo = flowRecordService.queryPage(caller, provider, pageStruct);
        List<RPCFlowRecord> records = pageInfo.getList();
        if (CollectionUtils.isEmpty(records)) {
            return new PageRpcFlowRecordStruct();
        }
        List<RpcFlowRecordStruct> structs = records.stream().map(CollectorServiceConverter.CONVERTER::convert).collect(Collectors.toList());
        return new PageRpcFlowRecordStruct(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), structs);
    }

    @Override
    public void deleteRpcFlowLogRecord(Long id) {
        AssertUtil.notNull(id, "Id should no be bull.");
        flowRecordService.deleteByPrimaryKey(id);
    }

    @Override
    public PageRpcExceptionRecordStruct pageRpcErrorLog(String application, String serviceClassName, Integer type, PageStruct struct) {
        PageInfo<RPCExceptionRecord> pageInfo = exceptionRecordService.queryPage(application, serviceClassName, type, struct);
        List<RPCExceptionRecord> records = pageInfo.getList();
        if (CollectionUtils.isEmpty(records)) {
            return new PageRpcExceptionRecordStruct();
        }
        List<RpcExceptionRecordStruct> structs = records.stream().map(CollectorServiceConverter.CONVERTER::convert).collect(Collectors.toList());
        return new PageRpcExceptionRecordStruct(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), structs);
    }

    @Override
    public void deleteRpcExceptionRecord(Long id) {
        AssertUtil.notNull(id, "Id should no be bull.");
        exceptionRecordService.deleteByPrimaryKey(id);
    }

}
