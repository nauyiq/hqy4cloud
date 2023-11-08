package com.hqy.cloud.auth.service.tk.support;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.auth.mapper.RoleTkMapper;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.auth.base.vo.AccountRoleVO;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.service.tk.RoleTkService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
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
    public BaseTkMapper<Role, Integer> getTkMapper() {
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
