package com.hqy.cloud.db.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/1
 */
public class MybatisDatetimeHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "created", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updated", Date.class, new Date());
    }
}
