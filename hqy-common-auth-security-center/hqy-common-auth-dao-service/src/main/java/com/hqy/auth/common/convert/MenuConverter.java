package com.hqy.auth.common.convert;

import com.hqy.auth.common.dto.MenuDTO;
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
public interface MenuConverter {

    MenuConverter CONVERTER = Mappers.getMapper(MenuConverter.class);

    @Mapping(target = "menuType", source = "type")
    @Mapping(target = "status", source = "status", qualifiedByName = "booleanToInteger")
    @Mapping(target = "label", source = "name")
    AdminTreeMenuVo convert(Menu menu);

    @Mapping(target = "type", source = "menuType")
    @Mapping(target = "status", source = "status", qualifiedByName = "IntegerToBoolean")
    Menu convert(MenuDTO menuDTO);


}
