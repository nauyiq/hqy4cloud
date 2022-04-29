package com.hqy.order.common.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.order.common.entity.Storage;
import com.hqy.rpc.transaction.GlobalTransactionalThriftMethod;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 14:16
 */
@ThriftService(MicroServiceConstants.TRANSACTION_STORAGE_SERVICE)
public interface StorageRemoteService extends RPCService {


    /**
     * 获取商品库存
     * @param storageId 库存id
     * @return
     */
    @ThriftMethod
    String getStorage(@ThriftField(1) Long storageId);

    /**
     * 修改storage
     * @param storage storage json
     * @return 是否成功修改
     */
    @ThriftMethod
    @GlobalTransactionalThriftMethod
    boolean modifyStorage(@ThriftField(1) String storage);


    @ThriftMethod
    @GlobalTransactionalThriftMethod
    boolean tccModifyStorage(@ThriftField(1) String beforeStorage, @ThriftField(2) String afterStorage);
}
