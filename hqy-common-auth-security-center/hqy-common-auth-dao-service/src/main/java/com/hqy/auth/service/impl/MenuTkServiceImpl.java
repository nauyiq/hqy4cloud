package com.hqy.auth.service.impl;

import com.hqy.auth.common.vo.menu.AdminMenuInfoVO;
import com.hqy.auth.dao.MenuDao;
import com.hqy.auth.entity.Menu;
import com.hqy.auth.service.MenuTkService;
import com.hqy.base.BaseDao;
import com.hqy.base.PrimaryLessTkDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.base.impl.PrimaryLessTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuTkServiceImpl extends BaseTkServiceImpl<Menu, Long> implements MenuTkService {

    private final MenuDao menuDao;

    @Override
    public BaseDao<Menu, Long> getTkDao() {
        return menuDao;
    }

    @Override
    public List<AdminMenuInfoVO> getAdminMenuInfoByParentId(long parentId) {
        return menuDao.getAdminMenuByParentId(parentId);
    }

}
