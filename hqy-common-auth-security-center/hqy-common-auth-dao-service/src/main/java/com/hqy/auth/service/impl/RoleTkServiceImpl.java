package com.hqy.auth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.auth.common.vo.AccountRoleVO;
import com.hqy.auth.dao.RoleDao;
import com.hqy.auth.entity.Role;
import com.hqy.auth.service.RoleTkService;
import com.hqy.base.BaseDao;
import com.hqy.base.common.result.PageResult;
import com.hqy.base.impl.BaseTkServiceImpl;
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

    private final RoleDao roleDao;

    @Override
    public BaseDao<Role, Integer> getTkDao() {
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
