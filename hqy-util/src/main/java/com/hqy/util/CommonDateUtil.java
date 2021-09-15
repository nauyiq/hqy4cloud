package com.hqy.util;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-15 16:15
 */
public class CommonDateUtil {

    private static final Logger log = LoggerFactory.getLogger(CommonDateUtil.class);

    /**
     * 时间 转 HH:mm格式
     * @param _date
     * @param date
     * @return
     */
    public static Date getHourAndMinute(Date _date, String date) {
        final DateFormat df = new SimpleDateFormat("HH:mm");
        try {
            final Date time = df.parse(date);
            final Calendar cal = Calendar.getInstance();
            cal.setTime(time);

            Date result = _date != null ? _date : new Date();
            result = DateUtils.setMinutes(result, cal.get(Calendar.MINUTE));
            result = DateUtils.setHours(result, cal.get(Calendar.HOUR_OF_DAY));
            return result;
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
