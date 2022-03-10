package com.hqy.gateway.server;

import com.hqy.fundation.limit.service.ThrottlesServer;
import com.hqy.security.access.service.RedisBiBlockedIpServiceImpl;
import com.hqy.security.access.service.RedisManualBlockedIpServiceImpl;
import com.hqy.security.access.service.RedisManualWhiteIpServiceImpl;
import com.hqy.util.HtmlCommonUtil;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 节流执行器
 * @author qy
 * @date 2021-08-02 10:45
 */
@Slf4j
public class ThrottlesProcess implements ThrottlesServer {

    private ThrottlesProcess() {}

    private static ThrottlesProcess instance = null;

    public static ThrottlesProcess getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (ThrottlesProcess.class) {
                if (Objects.isNull(instance)) {
                    instance = new ThrottlesProcess();
                }
            }
        }
        return instance;
    }

    /**
     * 校验参数
     */
    public static final int PARAMS_CHECK_MODE = 0;
    /**
     * 校验uri
     */
    public static final int URI_CHECK_MODE = 1;
    /**
     * IP 封禁时间 0.5个小时.....[黑客]
     */
    public static final int IP_ACCESS_BLOCK_SECONDS = 30 * 60;


    @Override
    public boolean isHackAccess(String paramStringOrUri, int mode) {

        if (StringUtils.isBlank(paramStringOrUri)) {
            return false;
        } else {
            paramStringOrUri = HtmlCommonUtil.htmlUnescape(paramStringOrUri);
        }

        String finalParamStringOrUri = paramStringOrUri;

        if (mode == PARAMS_CHECK_MODE) {
            //校验param参数
            return HtmlCommonUtil.HACK_WORDS_IN_PARAM.stream()
                    .anyMatch(word -> StringUtils.containsIgnoreCase(finalParamStringOrUri, word));
        }
        if (mode == URI_CHECK_MODE) {
            //校验uri
            return HtmlCommonUtil.HACK_WORDS_IN_URI.stream()
                    .anyMatch(word -> StringUtils.containsIgnoreCase(finalParamStringOrUri, word));
        }

        return false;
    }

    @Override
    public boolean isWhiteIp(String remoteAddress) {
        return RedisManualWhiteIpServiceImpl.getInstance().isWhiteIp(remoteAddress);
    }

    @Override
    public boolean isWhiteUri(String uri) {
        return false;
    }


    @Override
    public boolean isBiBlockedIp(String ip) {
        RedisBiBlockedIpServiceImpl service = SpringContextHolder.getBean(RedisBiBlockedIpServiceImpl.class);
        return service.isBlockIp(ip);
    }

    @Override
    public boolean isManualBlockedIp(String ip) {
        RedisManualBlockedIpServiceImpl service = SpringContextHolder.getBean(RedisManualBlockedIpServiceImpl.class);
        return service.isBlockIp(ip);
    }

    @Override
    public void addBiBlockIp(String ip, Integer blockSeconds) {
        RedisBiBlockedIpServiceImpl service = SpringContextHolder.getBean(RedisBiBlockedIpServiceImpl.class);
        service.addBlockIp(ip, blockSeconds);
    }

    @Override
    public void addManualBlockIp(String ip, Integer blockSeconds) {
        RedisManualBlockedIpServiceImpl service = SpringContextHolder.getBean(RedisManualBlockedIpServiceImpl.class);
        service.addBlockIp(ip, blockSeconds);
    }




}
