package com.hqy.collector.converter;

import com.hqy.base.common.base.converter.CommonConverter;
import com.hqy.coll.struct.PfExceptionStruct;
import com.hqy.coll.struct.ThrottledBlockStruct;
import com.hqy.collector.entity.PfException;
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



}
