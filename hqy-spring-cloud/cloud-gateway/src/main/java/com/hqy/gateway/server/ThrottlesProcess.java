package com.hqy.gateway.server;

import com.hqy.service.limit.ThrottlesServer;
import com.hqy.service.limit.impl.RedisBiBlockedIpService;
import com.hqy.service.limit.impl.RedisManualBlockedIpService;
import com.hqy.service.limit.impl.RedisManualWhiteIpService;
import com.hqy.util.HtmlCommonUtil;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-02 10:45
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
//        final String original = paramStringOrUri;
        if (StringUtils.isBlank(paramStringOrUri)) {
            return false;
        } else {
            paramStringOrUri = HtmlCommonUtil.htmlUnescape(paramStringOrUri);
        }
        String finalParamStringOrUri = paramStringOrUri;

        if (mode == PARAMS_CHECK_MODE) {
            return HtmlCommonUtil.HACK_WORDS_IN_PARAM.stream().anyMatch(word -> StringUtils.containsIgnoreCase(finalParamStringOrUri, word));
        }

        if (mode == URI_CHECK_MODE) {
            return HtmlCommonUtil.HACK_WORDS_IN_URI.stream().anyMatch(word -> StringUtils.containsIgnoreCase(finalParamStringOrUri, word));
        }
        return false;
    }

    @Override
    public boolean isWhiteIp(String remoteAddress) {
        return RedisManualWhiteIpService.getInstance().isWhiteIp(remoteAddress);
    }

    @Override
    public boolean isWhiteUri(String uri) {
        return false;
    }

    /**
     * 是否是bi分析后的拒绝访问的黑名单？
     * @param remoteAddr
     * @return
     */
    @Override
    public boolean isBIBlockedIp(String remoteAddr) {
        RedisBiBlockedIpService service = SpringContextHolder.getBean(RedisBiBlockedIpService.class);
        return service.isBlockIp(remoteAddr);
    }

    @Override
    public boolean isManualBlockedIp(String remoteAddr) {
        RedisManualBlockedIpService service = SpringContextHolder.getBean(RedisManualBlockedIpService.class);
        return service.isBlockIp(remoteAddr);
    }

    @Override
    public void addBiBlockIp(String remoteAddr, Integer blockSeconds) {
        RedisBiBlockedIpService service = SpringContextHolder.getBean(RedisBiBlockedIpService.class);
        service.addBlockIp(remoteAddr, blockSeconds);
    }

    @Override
    public void addManualBlockIp(String remoteAddr, Integer blockSeconds) {
        RedisManualBlockedIpService service = SpringContextHolder.getBean(RedisManualBlockedIpService.class);
        service.addBlockIp(remoteAddr, blockSeconds);
    }




}
