package com.hqy.coll.gateway.dao;

import com.hqy.base.BaseDao;
import com.hqy.coll.gateway.entity.ThrottledIpBlock;
import org.springframework.stereotype.Repository;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-10 14:03
 */
@Repository
public interface ThrottledIpBlockDao extends BaseDao<ThrottledIpBlock, Long> {
}
