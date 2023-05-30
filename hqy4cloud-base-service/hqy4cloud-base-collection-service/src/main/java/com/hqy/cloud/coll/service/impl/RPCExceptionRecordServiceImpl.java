package com.hqy.cloud.coll.service.impl;

import com.github.pagehelper.PageInfo;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.coll.mapper.RPCExceptionRecordTkMapper;
import com.hqy.cloud.coll.entity.RPCExceptionRecord;
import com.hqy.cloud.coll.service.RPCExceptionRecordService;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Objects;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.PERCENT;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/5 15:24
 */
@Service
@RequiredArgsConstructor
public class RPCExceptionRecordServiceImpl extends BaseTkServiceImpl<RPCExceptionRecord, Long> implements RPCExceptionRecordService {

    private final RPCExceptionRecordTkMapper dao;

    @Override
    public BaseTkMapper<RPCExceptionRecord, Long> getTkMapper() {
        return dao;
    }

    @Override
    public PageInfo<RPCExceptionRecord> queryPage(String application, String serviceClassName, Integer type, PageStruct struct) {
        Example example = new Example(RPCExceptionRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(application)) {
            criteria.andEqualTo("application", application);
        }
        if (StringUtils.isNotBlank(serviceClassName)) {
            criteria.andLike("serviceClassName", PERCENT + serviceClassName + PERCENT);
        }
        if (Objects.nonNull(type)) {
            criteria.andEqualTo("type", type);
        }
        example.orderBy("id").desc();
        List<RPCExceptionRecord> records = dao.selectByExample(example);
        if (CollectionUtils.isEmpty(records)) {
            return new PageInfo<>();
        }
        return new PageInfo<>(records);
    }
}
