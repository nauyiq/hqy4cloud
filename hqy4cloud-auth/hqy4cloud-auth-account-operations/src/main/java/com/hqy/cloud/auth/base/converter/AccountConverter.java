package com.hqy.cloud.auth.base.converter;

import com.hqy.cloud.account.response.AccountInfo;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.account.struct.AccountStruct;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.entity.AccountProfile;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.common.base.converter.CommonConverter;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/1 14:43
 */
@SuppressWarnings("all")
@Mapper(uses = CommonConverter.class,  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountConverter {
     AccountConverter CONVERTER = Mappers.getMapper(AccountConverter.class);

    @Mapping(target = "created", source = "created", qualifiedByName = "dateConvertLong")
    AccountStruct convert(AccountInfoDTO account);

    @Mapping(target = "birthday", source = "birthday", qualifiedByName = "dateConvertBirthday")
    AccountProfileStruct convertProfile(AccountInfoDTO account);

    @Mapping(target = "birthday", source = "birthday", qualifiedByName = "stringBirthday")
    void update(@MappingTarget AccountProfile profile, AccountProfileStruct struct);

    @Mapping(target = "birthday", source = "birthday", qualifiedByName = "dateConvertBirthday")
    AccountProfileStruct convert(AccountProfile profile);

    AccountInfo mapToVo(Account account);
}
