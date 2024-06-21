package com.hqy.cloud.auth.account.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.auth.account.mapper.ResourceMapper;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.auth.base.dto.ResourceDTO;
import com.hqy.cloud.auth.account.entity.Resource;
import com.hqy.cloud.auth.account.service.ResourceService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 18:00
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl extends BaseTkServiceImpl<Resource, Integer> implements ResourceService {

    private final ResourceMapper resourceDao;

    @Override
    public BaseTkMapper<Resource, Integer> getTkMapper() {
        return resourceDao;
    }

    @Override
    public PageResult<ResourceDTO> getPageResources(String name, Integer current, Integer size) {
        PageHelper.startPage(current, size);
        Example example = new Example(Resource.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deleted", 0);
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", "%".concat(name).concat("%"));
        }
        List<Resource> resources = resourceDao.selectByExample(example);
        if (CollectionUtils.isEmpty(resources)) {
            return new PageResult<>();
        }
        PageInfo<Resource> pageInfo = new PageInfo<>(resources);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(),
                resources.stream().map(ResourceDTO::new).collect(Collectors.toList()));
    }

}
