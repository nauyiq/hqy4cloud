package com.hqy.cloud.service.impl;

import com.hqy.cloud.common.vo.menu.AdminMenuInfoVO;
import com.hqy.cloud.mapper.MenuTkMapper;
import com.hqy.cloud.entity.Menu;
import com.hqy.cloud.service.MenuTkService;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.support.BaseTkServiceImpl;
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

    private final MenuTkMapper menuDao;

    @Override
    public BaseTkMapper<Menu, Long> getTkDao() {
        return menuDao;
    }

    @Override
    public List<AdminMenuInfoVO> getAdminMenuInfoByParentId(long parentId) {
        return menuDao.getAdminMenuByParentId(parentId);
    }

}
