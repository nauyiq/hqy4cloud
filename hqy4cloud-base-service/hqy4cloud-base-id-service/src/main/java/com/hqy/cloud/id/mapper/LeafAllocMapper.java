package com.hqy.cloud.id.mapper;

import com.hqy.cloud.id.entities.LeafAlloc;
import com.hqy.cloud.db.tk.PrimaryLessTkMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 15:10
 */
@Repository
public interface LeafAllocMapper extends PrimaryLessTkMapper<LeafAlloc> {

    /**
     * 获取所有的业务标志tags
     * @return bizTags
     */
    List<String> getAllTags();

}
