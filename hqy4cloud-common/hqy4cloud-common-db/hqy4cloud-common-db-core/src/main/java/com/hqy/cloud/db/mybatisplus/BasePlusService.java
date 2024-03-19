package com.hqy.cloud.db.mybatisplus;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 基于mybatis-plus的单表crud service
 * 如果使用mybatis-plus的批处理是需要开启rewriteBatchedStatements=true, 因为mybatis-plus批处理本质是for 遍历每条数据依次插入，但使用了批处理优化，默认是每 1000 条数据，刷新一次 statement 提交到数据库
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/29
 */
public interface BasePlusService<T extends BaseEntity> extends IService<T> {


}
