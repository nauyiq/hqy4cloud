package com.hqy.cloud.id.service;

import com.hqy.cloud.id.entities.LeafAlloc;
import com.hqy.cloud.db.tk.PrimaryLessTkService;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 15:11
 */
public interface LeafAllocTkService extends PrimaryLessTkService<LeafAlloc> {

    /**
     * 获取所有的业务标志tags
     * @return bizTags
     */
    List<String> getAllTags();

    /**
     * 更新最大id并且获取LeafAlloc
     * @param key biz_tag
     * @return    LeafAlloc.
     */
    LeafAlloc updateMaxIdAndGetLeafAlloc(String key);

    /**
     * updateMaxIdByCustomStepAndGetLeafAlloc
     * @param leafAlloc leafAlloc
     * @return LeafAlloc
     */
    LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);
}
