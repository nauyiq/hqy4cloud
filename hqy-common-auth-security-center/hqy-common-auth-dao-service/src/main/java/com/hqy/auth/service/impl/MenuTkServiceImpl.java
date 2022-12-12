package com.hqy.auth.service.impl;

import com.hqy.auth.dao.MenuDao;
import com.hqy.auth.entity.Menu;
import com.hqy.auth.service.MenuTkService;
import com.hqy.base.PrimaryLessTkDao;
import com.hqy.base.impl.PrimaryLessTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuTkServiceImpl extends PrimaryLessTkServiceImpl<Menu> implements MenuTkService {

    private final MenuDao menuDao;

    @Override
    public PrimaryLessTkDao<Menu> getTkDao() {
        return menuDao;
    }
}
