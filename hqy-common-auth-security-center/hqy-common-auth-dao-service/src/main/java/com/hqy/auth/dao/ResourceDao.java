package com.hqy.auth.dao;

import com.hqy.auth.entity.Resource;
import com.hqy.account.struct.ResourcesInRoleStruct;
import com.hqy.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 17:58
 */
@Repository
public interface ResourceDao extends BaseDao<Resource, Integer> {


}
