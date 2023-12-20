package com.hqy.cloud.notice.email;

import com.hqy.foundation.common.EventContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 16:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailContent implements EventContent {

    private String id;
    private String sender;
    private String subject;
    private String content;

    @Override
    public String getId() {
        return id;
    }
}
