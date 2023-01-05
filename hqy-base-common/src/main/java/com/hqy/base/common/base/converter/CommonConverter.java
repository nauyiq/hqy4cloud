package com.hqy.base.common.base.converter;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.mapstruct.Named;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/16 10:45
 */
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


}
