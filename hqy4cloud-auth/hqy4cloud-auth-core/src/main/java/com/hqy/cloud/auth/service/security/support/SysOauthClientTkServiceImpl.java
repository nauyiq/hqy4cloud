package com.hqy.cloud.auth.service.security.support;

import com.hqy.cloud.auth.mapper.SysOauthClientTkMapper;
import com.hqy.cloud.auth.entity.SysOauthClient;
import com.hqy.cloud.auth.service.tk.SysOauthClientTkService;
import com.hqy.cloud.tk.PrimaryLessTkMapper;
import com.hqy.cloud.tk.support.PrimaryLessTkServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:53
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOauthClientTkServiceImpl extends PrimaryLessTkServiceImpl<SysOauthClient> implements SysOauthClientTkService {

    @Resource
    private final SysOauthClientTkMapper sysOauthClientTkMapper;

    @Override
    public PrimaryLessTkMapper<SysOauthClient> getTkDao() {
        return sysOauthClientTkMapper;
    }

}
