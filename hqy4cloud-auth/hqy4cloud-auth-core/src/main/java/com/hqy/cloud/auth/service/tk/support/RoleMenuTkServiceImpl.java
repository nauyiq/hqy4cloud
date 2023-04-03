package com.hqy.cloud.auth.service.tk.support;

import com.hqy.cloud.auth.mapper.RoleMenuMapper;
import com.hqy.cloud.auth.entity.RoleMenu;
import com.hqy.cloud.auth.service.tk.RoleMenuTkService;
import com.hqy.cloud.tk.PrimaryLessTkMapper;
import com.hqy.cloud.tk.support.PrimaryLessTkServiceImpl;
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
public class RoleMenuTkServiceImpl extends PrimaryLessTkServiceImpl<RoleMenu> implements RoleMenuTkService {

    private final RoleMenuMapper dao;


    @Override
    public PrimaryLessTkMapper<RoleMenu> getTkDao() {
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
