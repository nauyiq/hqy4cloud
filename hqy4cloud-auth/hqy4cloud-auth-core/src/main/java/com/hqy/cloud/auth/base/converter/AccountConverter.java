package com.hqy.cloud.auth.base.converter;

import com.hqy.cloud.account.struct.AccountStruct;
import com.hqy.cloud.auth.base.dto.AccountDTO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.common.base.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/1 14:43
 */
@SuppressWarnings("all")
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountConverter {
     AccountConverter CONVERTER = Mappers.getMapper(AccountConverter.class);


    AccountStruct convert(AccountDTO account);

    @Mapping(target = "created", source = "created", qualifiedByName = "dateConvertLong")
    AccountStruct convert(Account account);






}
