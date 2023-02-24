package com.hqy.collector.dao;

import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.collector.entity.ThrottledBlock;
import org.springframework.stereotype.Repository;

/**
 * @author qy
 * @date 2021-08-10 14:03
 */
@Repository
public interface ThrottledBlockTkMapper extends BaseTkMapper<ThrottledBlock, Long> {
}
