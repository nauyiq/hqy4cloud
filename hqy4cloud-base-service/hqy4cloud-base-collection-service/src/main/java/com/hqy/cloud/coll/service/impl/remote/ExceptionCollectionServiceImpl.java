package com.hqy.cloud.coll.service.impl.remote;

import com.github.pagehelper.PageInfo;
import com.hqy.cloud.coll.struct.PageExceptionLogStruct;
import com.hqy.cloud.coll.struct.PfExceptionStruct;
import com.hqy.cloud.coll.converter.CollectorServiceConverter;
import com.hqy.cloud.coll.entity.PfException;
import com.hqy.cloud.coll.service.PfExceptionService;
import com.hqy.cloud.coll.service.ExceptionCollectionService;
import com.hqy.cloud.rpc.thrift.service.AbstractRPCService;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 17:39
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExceptionCollectionServiceImpl extends AbstractRPCService implements ExceptionCollectionService {

    private final PfExceptionService pfExceptionService;

    @Override
    public void collect(PfExceptionStruct struct) {
        if (Objects.isNull(struct)) {
            log.warn("PfExceptionStruct should not be null.");
            return;
        }
        PfException pfException = CollectorServiceConverter.CONVERTER.convert(struct);
        pfExceptionService.insert(pfException);
    }

    @Override
    public PageExceptionLogStruct queryPage(String serviceName, String type, String env, String exceptionClass, String ip, String url, PageStruct struct) {
        PageInfo<PfException> pageInfo = pfExceptionService.queryPage(serviceName, type, env, exceptionClass, ip, url, struct);
        List<PfException> pfExceptions = pageInfo.getList();
        PageExceptionLogStruct pageExceptionLogStruct;
        if (CollectionUtils.isEmpty(pfExceptions)) {
            pageExceptionLogStruct = new PageExceptionLogStruct();
        } else {
            List<PfExceptionStruct> structs = pfExceptions.stream().map(CollectorServiceConverter.CONVERTER::convert).collect(Collectors.toList());
            pageExceptionLogStruct = new PageExceptionLogStruct(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), structs);
        }
        return pageExceptionLogStruct;
    }

    @Override
    public void deleteErrorLog(Long id) {
        AssertUtil.notNull(id, "Id should not be null.");
        pfExceptionService.deleteByPrimaryKey(id);
    }
}
