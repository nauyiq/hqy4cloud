package com.hqy.cloud.auth.account.entity.convertor;

import com.hqy.cloud.account.response.AccountInfo;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.common.base.converter.CommonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/11
 */
@Mapper(uses = CommonConverter.class, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AccountConvertor {

    AccountConvertor CONVERTOR = Mappers.getMapper(AccountConvertor.class);

    AccountInfo mapToVo(AccountInfoDTO account);




}
