package com.hqy.storage.service;

import com.hqy.base.BaseTkService;
import com.hqy.common.entity.Storage;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 14:15
 */
public interface StorageTkService extends BaseTkService<Storage, Long> {

    /**
     * CAS
     * @param productId
     * @param use
     * @param residue
     * @param beforeResidue
     * @return
     */
    boolean casUpdate(Long productId, int use, int residue, Integer beforeResidue);
}
