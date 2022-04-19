package com.hqy.coll.dao;

import com.hqy.base.BaseDao;
import com.hqy.coll.entity.ThrottledIpBlock;
import org.springframework.stereotype.Repository;

/**
 * @author qy
 * @date 2021-08-10 14:03
 */
@Repository
public interface ThrottledIpBlockDao extends BaseDao<ThrottledIpBlock, Long> {
}
