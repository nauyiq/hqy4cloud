package com.hqy.cloud.auth.infrastructure.certification.service.support;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.hqy.cloud.auth.infrastructure.certification.config.CertificationConfigProperties;
import com.hqy.cloud.auth.infrastructure.certification.service.AuthService;
import com.hqy.cloud.util.HttpRestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author hongqy
 * @date 2025/2/14
 */
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final CertificationConfigProperties certificationConfigProperties;
    private static final String STATE = "state";
    @Override
    public boolean checkAuth(String realName, String idCard) {
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(2);
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + certificationConfigProperties.getAppcode());
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> query = Maps.newHashMapWithExpectedSize(2);
        Map<String, Object> body = Maps.newHashMapWithExpectedSize(2);
        body.put("id_number", idCard);
        body.put("name", realName);
        try ( HttpResponse response = HttpRestUtil.doPostForm(certificationConfigProperties.getHost(), certificationConfigProperties.getPath(), headers, query, body)) {
            JSONObject jsonObject = JSON.parseObject(response.body());
            log.info("Auth result : {}", jsonObject);
            if ((Integer)jsonObject.get(STATE) == 1) {
                return true;
            }
        } catch (Exception e) {
            log.error("checkAuth error realName={}", realName, e);
        }
        return false;
    }
}
