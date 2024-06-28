package com.hqy.cloud.auth.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hqy.cloud.auth.account.entity.AccountMenu;
import com.hqy.cloud.auth.account.mapper.AccountMenuMapper;
import com.hqy.cloud.auth.account.service.AccountMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/27
 */
@Slf4j
@Service
public class AccountMenuServiceImpl extends ServiceImpl<AccountMenuMapper, AccountMenu> implements AccountMenuService {
}
