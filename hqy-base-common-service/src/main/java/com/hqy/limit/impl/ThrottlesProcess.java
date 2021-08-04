package com.hqy.limit.impl;

import com.hqy.limit.BiBlockedIpService;
import com.hqy.limit.ManualBlockedIpService;
import com.hqy.limit.ThrottlesServer;
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
        return false;
    }

    /**
     * 是否是bi分析后的拒绝访问的黑名单？
     * @param remoteAddr
     * @return
     */
    @Override
    public boolean isBIBlockedIp(String remoteAddr) {
        BiBlockedIpService service = SpringContextHolder.getBean(RedisBiBlockedIpService.class);
        return service.isBlockIp(remoteAddr);
    }

    @Override
    public boolean isManualBlockedIp(String remoteAddr) {
        ManualBlockedIpService service = SpringContextHolder.getBean(RedisManualBlockedIpService.class);
        return service.isBlockIp(remoteAddr);
    }

    @Override
    public void addBiBlockIp(String remoteAddr, Integer blockSeconds) {
        BiBlockedIpService biBlockedIpService = SpringContextHolder.getBean(BiBlockedIpService.class);
        biBlockedIpService.addBlockIp(remoteAddr, blockSeconds);
    }

    @Override
    public void addManualBlockIp(String remoteAddr, Integer blockSeconds) {
        ManualBlockedIpService manualBlockedIpService = SpringContextHolder.getBean(ManualBlockedIpService.class);
        manualBlockedIpService.addBlockIp(remoteAddr, blockSeconds);
    }

    /**
     * 记录封禁 行为日志，历史记录，方便将来查看...
     * @param ip 被封禁的ip
     * @param blockSeconds 被堵塞的时间时长，秒
     * @param url 被拦截时的访问url  例如是人工指定？还是HttpThrottleFilter(发现了恶意访问)，还是BIBlock(恶意关键词等..)
     * @param createdBy 阻塞操作的组件或者逻辑
     * @param accessParamJson 请求参数json
     */
    public void persistBlockIpAction(String ip, Integer blockSeconds, String url, String createdBy, String accessParamJson) {
        //TODO
    }


}
