package com.hqy.auth.common.convert;

import com.hqy.auth.common.vo.menu.AdminTreeMenuVo;
import com.hqy.auth.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @date 2022/12/16 10:35 
 * @version 1.0
 */
@Mapper(uses = CommonConverter.class)
public interface TreeMenuVoConverter {

    TreeMenuVoConverter CONVERTER = Mappers.getMapper(TreeMenuVoConverter.class);

    @Mapping(target = "menuType", source = "type")
    AdminTreeMenuVo convert(Menu menu);


}
