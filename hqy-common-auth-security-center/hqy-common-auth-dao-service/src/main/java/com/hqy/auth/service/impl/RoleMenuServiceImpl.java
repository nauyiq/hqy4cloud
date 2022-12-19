package com.hqy.auth.service.impl;

import com.hqy.auth.dao.RoleMenuDao;
import com.hqy.auth.entity.RoleMenu;
import com.hqy.auth.service.RoleMenuService;
import com.hqy.base.PrimaryLessTkDao;
import com.hqy.base.impl.PrimaryLessTkServiceImpl;
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
public class RoleMenuServiceImpl extends PrimaryLessTkServiceImpl<RoleMenu> implements RoleMenuService {

    private final RoleMenuDao dao;


    @Override
    public PrimaryLessTkDao<RoleMenu> getTkDao() {
        return dao;
    }

    @Override
    public boolean deleteByRoleId(Integer id) {
        Example example = new Example(RoleMenu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleId", id);
        return dao.deleteByExample(example) > 0;
    }
}
