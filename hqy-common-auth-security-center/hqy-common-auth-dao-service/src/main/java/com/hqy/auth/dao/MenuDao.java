package com.hqy.auth.dao;

import com.hqy.auth.entity.Menu;
import com.hqy.base.PrimaryLessTkDao;
import org.springframework.stereotype.Repository;

/**
 * MenuDao.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 19:18
 */
@Repository
public interface MenuDao extends PrimaryLessTkDao<Menu> {
}
