package com.hqy.cloud.auth.service.tk.support;

import com.hqy.cloud.auth.mapper.MenuTkMapper;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.entity.Menu;
import com.hqy.cloud.auth.service.tk.MenuTkService;
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
