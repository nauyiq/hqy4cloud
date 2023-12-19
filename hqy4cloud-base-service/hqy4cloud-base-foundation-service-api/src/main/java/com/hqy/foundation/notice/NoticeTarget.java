package com.hqy.foundation.notice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知的目标
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 16:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeTarget {

    private String ns;
    private String group;
    private String target;

    public static NoticeTarget of(String target) {
        return new NoticeTarget(null, null, target);
    }
}
