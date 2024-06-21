package com.hqy.cloud.auth.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hqy.cloud.auth.account.mapper.RoleMenuMapper;
import com.hqy.cloud.auth.account.entity.RoleMenu;
import com.hqy.cloud.auth.account.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:25
 */
@Service
@RequiredArgsConstructor
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
    private final RoleMenuMapper mapper;

    @Override
    public boolean deleteByRoleId(Integer id) {
        Example example = new Example(RoleMenu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleId", id);
        return mapper.deleteByExample(example) > 0;
    }
}
