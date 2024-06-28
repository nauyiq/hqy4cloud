package com.hqy.cloud.auth.base.converter;

import com.hqy.cloud.auth.account.entity.Permissions;
import com.hqy.cloud.auth.base.dto.MicroServiceType;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.vo.AdminResourceVO;
import com.hqy.cloud.common.base.converter.CommonConverter;
import com.hqy.cloud.common.base.project.ProjectInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * ResourceConverter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/20 13:44
 */
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResourceConverter {

    ResourceConverter CONVERTER = Mappers.getMapper(ResourceConverter.class);

    /**
     * Resource convert to AdminResourceVO.
     * @param permissions Resource.
     * @return         {@link AdminResourceVO}
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "statusConvertString")
    AdminResourceVO convert(Permissions permissions);

    /**
     * ResourceDTO convert to Resource.
     * @param resourceDTO {@link ResourceDTO}
     * @return            {@link Permissions}
     */
    Permissions convert(ResourceDTO resourceDTO);

    /**
     * ProjectInfo convert to MicroServiceType.
     * @param projectInfo {@link ProjectInfo}
     * @return            {@link MicroServiceType}
     */
    @Mapping(target = "label", source = "name")
    MicroServiceType convert(ProjectInfo projectInfo);

    /**
     * update resource from resourceDTO， not set null property.
     * @param resourceDTO {@link ResourceDTO}
     * @param permissions    {@link Permissions}
     */
    void updateResourceByDTO(ResourceDTO resourceDTO, @MappingTarget Permissions permissions);

}
