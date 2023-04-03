package com.hqy.cloud.auth.base.converter;

import com.hqy.cloud.auth.base.dto.MicroServiceType;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.base.vo.AdminResourceVO;
import com.hqy.cloud.auth.entity.Resource;
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
     * @param resource Resource.
     * @return         {@link AdminResourceVO}
     */
    @Mapping(target = "status", source = "status", qualifiedByName = "statusConvertString")
    AdminResourceVO convert(Resource resource);

    /**
     * ResourceDTO convert to Resource.
     * @param resourceDTO {@link ResourceDTO}
     * @return            {@link Resource}
     */
    Resource convert(ResourceDTO resourceDTO);

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
     * @param resource    {@link Resource}
     */
    void updateResourceByDTO(ResourceDTO resourceDTO, @MappingTarget Resource resource);

}
