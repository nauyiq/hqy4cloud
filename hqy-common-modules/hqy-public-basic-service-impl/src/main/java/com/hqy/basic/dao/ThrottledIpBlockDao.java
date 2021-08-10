package com.hqy.basic.dao;

import com.hqy.basic.entity.ThrottledIpBlock;
import com.hqy.dao.base.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-10 14:03
 */
@Repository
public interface ThrottledIpBlockDao extends BaseDao<ThrottledIpBlock, Long> {
}
