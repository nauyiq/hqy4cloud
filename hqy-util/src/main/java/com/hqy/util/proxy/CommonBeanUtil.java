package com.hqy.util.proxy;

import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-14 17:28
 */
public class CommonBeanUtil {

    private static final Logger log = LoggerFactory.getLogger(CommonBeanUtil.class);


    public static <T> T map2Bean(Map<String, Object> map, Class<T> tClass) {
        try {
            T instance = tClass.newInstance();
            BeanUtils.populate(instance, map);
            return instance;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
