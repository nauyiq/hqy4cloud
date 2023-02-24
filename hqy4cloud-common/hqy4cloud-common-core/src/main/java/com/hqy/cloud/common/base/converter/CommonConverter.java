package com.hqy.cloud.common.base.converter;

import com.hqy.cloud.common.base.lang.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mapstruct.Named;

import java.text.ParseException;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/16 10:45
 */
@Slf4j
public class CommonConverter {

    @Named("booleanToInteger")
    public static Integer booleanToInteger(Boolean status) {
        return status ? 1 : 0;
    }

    @Named("IntegerToBoolean")
    public static Boolean IntegerToBoolean(Integer status) {
        return status == 1;
    }

    @Named("statusConvertString")
    public static String statusConvertString(Boolean status) {
        return status.toString();
    }

    @Named("dateConvertString")
    public static String dateConvertString(Date date) {return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");}

    @Named("StringConvertDate")
    public static Date StringConvertDate(String date) {
        try {
            return DateUtils.parseDate(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            return null;
        }
    }

    @Named("timeStampConvertString")
    public static String timeStampConvertString(Long timestamp) {
        try {
            Date date = new Date(timestamp);
            return dateConvertString(date);
        } catch (Throwable cause) {
            log.error(cause.getMessage());
            return StringConstants.EMPTY;
        }
    }

}
