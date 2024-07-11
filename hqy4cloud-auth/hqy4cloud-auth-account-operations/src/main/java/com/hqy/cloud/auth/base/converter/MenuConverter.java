package com.hqy.cloud.auth.base.converter;

import com.hqy.cloud.auth.base.vo.AdminTreeMenuVO;
import com.hqy.cloud.common.base.converter.CommonConverter;
import com.hqy.cloud.auth.base.dto.MenuDTO;
import com.hqy.cloud.auth.account.entity.Menu;
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

    /**
     * Menu convert AdminTreeMenuVo
     * @param menu {@link Menu}
     * @return     {@link AdminTreeMenuVO}
     */
    @Mapping(target = "menuType", source = "type")
    @Mapping(target = "status", source = "status", qualifiedByName = "booleanToInteger")
    @Mapping(target = "label", source = "name")
    AdminTreeMenuVO convert( Menu menu);

    /**
     * MenuDTO convert Menu
     * @param menuDTO {@link MenuDTO}
     * @return        {@link Menu}
     */
    @Mapping(target = "type", source = "menuType")
    @Mapping(target = "status", source = "status", qualifiedByName = "IntegerToBoolean")
    Menu convert(MenuDTO menuDTO);


}
