package com.hqy.auth.common.convert;

import org.mapstruct.Named;

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


}
