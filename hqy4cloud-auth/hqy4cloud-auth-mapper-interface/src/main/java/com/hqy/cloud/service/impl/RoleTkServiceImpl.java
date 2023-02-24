package com.hqy.cloud.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.common.vo.AccountRoleVO;
import com.hqy.cloud.mapper.RoleTkMapper;
import com.hqy.cloud.entity.Role;
import com.hqy.cloud.service.RoleTkService;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.support.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
@RequiredArgsConstructor
public class RoleTkServiceImpl extends BaseTkServiceImpl<Role, Integer> implements RoleTkService {

    private final RoleTkMapper roleDao;

    @Override
    public BaseTkMapper<Role, Integer> getTkDao() {
        return roleDao;
    }

    @Override
    public List<Integer> selectIdByNames(List<String> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return null;
        }
        return roleDao.selectIdByNames(roleList);
    }

    @Override
    public List<Role> queryRolesByNames(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        return roleDao.queryRolesByNames(roles);
    }

    @Override
    public List<Role> getRolesList(Integer maxRoleLevel, Boolean status) {
        return roleDao.queryRoles(maxRoleLevel, status);
    }

    @Override
    public PageResult<AccountRoleVO> getPageRoles(String roleName, String note, Integer maxRoleLevel, Integer current, Integer size) {
        PageHelper.startPage(current, size);
        List<AccountRoleVO> accountRoleVOS = roleDao.getPageRoleVo(roleName, note, maxRoleLevel);
        if (CollectionUtils.isEmpty(accountRoleVOS)) {
            return new PageResult<>();
        }
        PageInfo<AccountRoleVO> pageInfo = new PageInfo<>(accountRoleVOS);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList());
    }
}
