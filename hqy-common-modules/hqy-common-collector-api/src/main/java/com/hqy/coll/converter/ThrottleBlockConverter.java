package com.hqy.coll.converter;

import com.hqy.base.common.base.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 10:44
 */
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ThrottleBlockConverter {

    ThrottleBlockConverter CONVERTER = Mappers.getMapper(ThrottleBlockConverter.class);






}
