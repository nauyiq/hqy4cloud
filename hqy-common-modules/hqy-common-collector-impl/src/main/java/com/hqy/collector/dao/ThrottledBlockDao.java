package com.hqy.collector.dao;

import com.hqy.base.BaseDao;
import com.hqy.collector.entity.ThrottledBlock;
import org.springframework.stereotype.Repository;

/**
 * @author qy
 * @date 2021-08-10 14:03
 */
@Repository
public interface ThrottledBlockDao extends BaseDao<ThrottledBlock, Long> {
}
