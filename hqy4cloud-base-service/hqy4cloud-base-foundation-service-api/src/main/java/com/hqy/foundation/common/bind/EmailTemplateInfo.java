package com.hqy.foundation.common.bind;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 16:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateInfo {

    /**
     * 主题.
     */
    private String subject;

    /**
     * 内容.
     */
    private String content;

}
