package com.hqy.cloud.auth.infrastructure.certification.service;

/**
 * @author hongqy
 * @date 2025/2/14
 */
public interface AuthService {

    /**
     * 检查认证信息
     * @param realName 真是姓名
     * @param idCard   身份证
     * @return         是否通过
     */
    boolean checkAuth(String realName, String idCard);

}
