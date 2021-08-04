package com.hqy.global.flow;

import com.hqy.cache.redis.LettuceRedisUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 基于Redis的流量控制器
 * redis基于spring data redis的redisTemplate
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-04 16:43
 */
@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class RedisFlowController {

    /**
     ** 最大访问次数限制
     */
    private int max = 20;

    /**
     ** 计数缓存有效期 (5分钟)
     */
    private long expireSeconds = 5 * 60;


    private static final String KEY_PREFIX_STRING  = RedisFlowController.class.getSimpleName().concat(":");

    /**
     * 是否访问超限?
     * @param limitRedisKey 自定义一个表示资源访问限次的串，例如  ip.concat(":").concat(小时 + "_" + 分钟);不能为null
     * @return
     */
    public RedisFlowDTO isOverLimit(String limitRedisKey) {
        if (!limitRedisKey.startsWith(KEY_PREFIX_STRING)) {
            limitRedisKey = KEY_PREFIX_STRING.concat(limitRedisKey);
        }
        long total = 0L;
        try {
            //如果redis目前没有这个key，创建并赋予1，有效时间为60s
            Boolean result = LettuceRedisUtil.stringSetEx(limitRedisKey, "1", expireSeconds);
            if (result) { //设置成功 则说明当前ip在时间窗口内首次访问
                total = 1L;
            } else {
                //当前key对应的值加1 并且返回加1后的值
                 total = Objects.requireNonNull(LettuceRedisUtil.incr(limitRedisKey, 1L));
                //Redis TTL命令以秒为单位返回key的剩余过期时间。当key不存在时，返回-2。当key存在但没有设置剩余生存时间时，返回-1。否则，以秒为单位，返回key的剩余生存时间。
                Long expire = LettuceRedisUtil.getExpire(limitRedisKey);
                if (Objects.nonNull(expire) && -1L == expire) {
                    //为给定key设置生存时间
                    LettuceRedisUtil.expire(limitRedisKey, expireSeconds);
                }
            }
        } catch (Exception e) {
            //如果redis 有异常，catch住，当做未超限来处理..................
            log.error("流量控制组件:执行计数操作失败,无法执行计数", e);
        }
        //判断是否大于阈值, 超过返回false
        if (total >= max) {
            return new RedisFlowDTO(limitRedisKey, true, total, max);
        }
        return new RedisFlowDTO(limitRedisKey, false, total, max);
    }


}
