package com.hqy.cloud.notice.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 16:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailContent {

    private String sender;
    private String subject;
    private String content;



}
