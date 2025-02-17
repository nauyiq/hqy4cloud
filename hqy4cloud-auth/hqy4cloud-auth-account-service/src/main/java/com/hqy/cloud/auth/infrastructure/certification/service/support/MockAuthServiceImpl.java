package com.hqy.cloud.auth.infrastructure.certification.service.support;

import com.hqy.cloud.auth.infrastructure.certification.service.AuthService;

/**
 * @author hongqy
 * @date 2025/2/14
 */
public class MockAuthServiceImpl implements AuthService {

    @Override
    public boolean checkAuth(String realName, String idCard) {
        return true;
    }
}
