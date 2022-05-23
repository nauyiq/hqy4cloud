package com.hqy.storage.dao;

import com.hqy.base.BaseDao;
import com.hqy.common.entity.Storage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 14:15
 */
@Repository
public interface StorageDao extends BaseDao<Storage, Long> {

    long casUpdate(@Param("productId")Long productId, @Param("user") int use,  @Param("residue")int residue, @Param("beforeResidue") Integer beforeResidue);
}
