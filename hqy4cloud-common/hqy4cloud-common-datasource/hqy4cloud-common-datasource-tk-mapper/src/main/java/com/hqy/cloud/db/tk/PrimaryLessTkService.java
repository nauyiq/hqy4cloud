package com.hqy.cloud.db.tk;

import java.util.List;

/**
 * 无主键策略的tk service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 9:35
 */
public interface PrimaryLessTkService<T extends PrimaryLessBaseEntity> {

    /**
     * 根据id查询数据
     * @param id 主键id
     * @return   实体对象
     */
    T queryById(Object id);

    /**
     * 根据实体属性查找 t不能为null
     * @param t 实体Entity
     * @return 返回唯一确定的一行数据
     */
    T queryOne(T t);


    /**
     * 根据实体属性查找 t不能为null
     * @param t 实体Entity
     * @return 返回实体的集合
     */
    List<T> queryList(T t);


    /**
     * 查询所有
     * @return 查询表中所有的数据.
     */
    List<T> queryAll();



    /**
     * 插入一行数据到数据库
     * @param t 实体对象
     * @return 是否插入数据库成功
     */
    boolean insert(T t);


    /**
     * 批量插入数据到数据库
     * @param entities 数据列表
     * @return 是否插入数据库成功
     */
    boolean insertList(List<T> entities);


    /**
     * 修改数据库的数据
     * @param t 实体对象
     * @return 是否修改数据成功
     */
    boolean update(T t);

    /**
     * 根据主键更新不为null的属性
     * @param t 实体对象.
     * @return  是否修改数据成功.
     */
    boolean updateSelective(T t);


    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     * @param t t   实体对象
     * @return      是否删除数据成功
     */
    boolean delete(T t);

    /**
     * 返回tk-mapper
     * @return {@link BaseTkMapper}
     */
    PrimaryLessTkMapper<T> getTkDao();

}
