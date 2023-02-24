package com.hqy.collector.converter;

import com.hqy.cloud.common.base.converter.CommonConverter;
import com.hqy.coll.struct.PfExceptionStruct;
import com.hqy.coll.struct.RpcExceptionRecordStruct;
import com.hqy.coll.struct.RpcFlowRecordStruct;
import com.hqy.coll.struct.ThrottledBlockStruct;
import com.hqy.collector.entity.PfException;
import com.hqy.collector.entity.RPCExceptionRecord;
import com.hqy.collector.entity.RPCFlowRecord;
import com.hqy.collector.entity.ThrottledBlock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 11:18
 */
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CollectorServiceConverter {

    CollectorServiceConverter CONVERTER =  Mappers.getMapper(CollectorServiceConverter.class);

    /**
     * ThrottledBlock convert to ThrottledBlockStruct.
     * @param throttledBlock {@link ThrottledBlock}
     * @return               {@link ThrottledBlockStruct}
     */
    @Mapping(target = "created", source = "created", qualifiedByName = "dateConvertString")
    ThrottledBlockStruct convert(ThrottledBlock throttledBlock);

    /**
     * PfExceptionStruct convert to PfException.
     * @param struct {@link PfExceptionStruct}
     * @return       {@link PfException}
     */
    @Mapping(target = "created", source = "created", qualifiedByName = "StringConvertDate")
    PfException convert(PfExceptionStruct struct);

    /**
     * PfException convert to PfExceptionStruct.
     * @param pfException {@link PfException}
     * @return            {@link PfExceptionStruct}
     */
    @Mapping(target = "created", source = "created", qualifiedByName = "dateConvertString")
    PfExceptionStruct convert(PfException pfException);

    /**
     * RPCFlowRecord convert to RpcFlowRecordStruct.
     * @param record {@link RPCFlowRecord}
     * @return       {@link RpcFlowRecordStruct}
     */
    @Mapping(target = "created", source = "created", qualifiedByName = "dateConvertString")
    RpcFlowRecordStruct convert(RPCFlowRecord record);


    /**
     * RPCExceptionRecord convert to RpcExceptionRecordStruct.
     * @param record {@link RPCExceptionRecord}
     * @return       {@link RpcExceptionRecordStruct}
     */
    @Mapping(target = "requestTime", source = "requestTime", qualifiedByName = "timeStampConvertString")
    RpcExceptionRecordStruct convert(RPCExceptionRecord record);



}
