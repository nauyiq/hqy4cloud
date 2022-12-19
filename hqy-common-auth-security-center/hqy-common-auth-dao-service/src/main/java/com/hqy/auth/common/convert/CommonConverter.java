package com.hqy.auth.common.convert;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/16 10:45
 */
public class CommonConverter {

    public Integer booleanConvert(boolean value) {
        return value ? 1 : 0;
    }

    public String dateConvert(Date date) {
        return DateUtil.formatDateTime(date);
    }

}
