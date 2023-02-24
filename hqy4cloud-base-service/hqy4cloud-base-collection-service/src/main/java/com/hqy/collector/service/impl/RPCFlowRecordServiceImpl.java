package com.hqy.collector.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.support.BaseTkServiceImpl;
import com.hqy.collector.dao.RPCMinuteFlowRecordTkMapper;
import com.hqy.collector.entity.RPCFlowRecord;
import com.hqy.collector.service.RPCFlowRecordService;
import com.hqy.rpc.thrift.struct.PageStruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.PERCENT;

/**
 * RPCFlowRecordServiceImpl.
 * @author qiyuan.hong
 * @date 2022-03-17 21:27
 */
@Service
@RequiredArgsConstructor
public class RPCFlowRecordServiceImpl extends BaseTkServiceImpl<RPCFlowRecord, Long> implements RPCFlowRecordService {

    private final RPCMinuteFlowRecordTkMapper dao;

    @Override
    public BaseTkMapper<RPCFlowRecord, Long> getTkDao() {
        return dao;
    }

    @Override
    public PageInfo<RPCFlowRecord> queryPage(String caller, String provider, PageStruct pageStruct) {
        Example example = new Example(RPCFlowRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(caller)) {
            criteria.andEqualTo("caller",  caller);
        }
        if (StringUtils.isNotBlank(provider)) {
            criteria.andLike("provider", PERCENT + provider + PERCENT);
        }
        example.orderBy("id").desc();
        PageHelper.startPage(pageStruct.pageNumber, pageStruct.pageSize);
        List<RPCFlowRecord> rpcFlowRecords = dao.selectByExample(example);
        if (CollectionUtils.isEmpty(rpcFlowRecords)) {
            return new PageInfo<>();
        }
        return new PageInfo<>(rpcFlowRecords);
    }
}
