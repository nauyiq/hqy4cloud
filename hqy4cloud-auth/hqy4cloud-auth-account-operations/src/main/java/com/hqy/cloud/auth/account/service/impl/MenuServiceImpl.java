package com.hqy.cloud.auth.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hqy.cloud.auth.base.vo.AdminMenuInfoVO;
import com.hqy.cloud.auth.account.entity.Menu;
import com.hqy.cloud.auth.account.mapper.MenuMapper;
import com.hqy.cloud.auth.account.service.MenuService;
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
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    private final MenuMapper mapper;

    @Override
    public List<AdminMenuInfoVO> getAdminMenuInfoByParentId(long parentId) {
        return mapper.getAdminMenuByParentId(parentId);
    }

}
