package com.hqy.cloud.util;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类 <br/>
 * 切记：SimpleDateFormat是线程不安全的 使用的时候最好确保是在同步的环境下使用的
 * 因此这也是官方为什么不推荐把SimpleDateFormat当做静态常量定义.
 * @author qiyuan.hong
 * @date  2021-09-15 16:15
 */
public class CommonDateUtil {

    private static final Logger log = LoggerFactory.getLogger(CommonDateUtil.class);
    private static final String SDF_PATTERN = "yyyyMMdd";

    public static String today() {
        return DateUtil.format(new Date(), SDF_PATTERN);
    }

    public static Date getHourAndMinute(Date date, String dateString) {
        final DateFormat df = new SimpleDateFormat("HH:mm");
        try {
            final Date time = df.parse(dateString);
            final Calendar cal = Calendar.getInstance();
            cal.setTime(time);

            Date result = date != null ? date : new Date();
            result = DateUtils.setMinutes(result, cal.get(Calendar.MINUTE));
            result = DateUtils.setHours(result, cal.get(Calendar.HOUR_OF_DAY));
            return result;
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
