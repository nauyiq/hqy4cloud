package com.hqy.cloud.db.tk;

import com.hqy.cloud.db.tk.model.BaseEntity;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 基于tk的单表crud service
 * T为对应的Entity, PK为主键类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 17:46
 */
public interface BaseTkService <T extends BaseEntity<PK>, PK> {

    /**
     * 根据id查找
     * @param pk primary key
     * @return 实体Entity
     */
    T queryById(PK pk);


    /**
     * 根据实体属性查找 t不能为null
     * @param t 实体Entity
     * @return 返回唯一确定的一行数据
     */
    T queryOne(T t);

    /**
     * 根据主键批量查询,
     * @param pks    主键集合
     * @return       返回实体的集合
     */
    List<T> queryByIds(List<PK> pks);

    /**
     * 根据主键批量查询,
     * @param pkName 主键属性名, 默认id
     * @param pks    主键集合
     * @return       返回实体的集合
     */
    List<T> queryByIds(String pkName, List<PK> pks);


    /**
     * 根据实体属性查找 t不能为null
     * @param t 实体Entity
     * @return 返回实体的集合
     */
    List<T> queryList(T t);

    /**
     * 根据条件查询.
     * @param example Tk Example
     * @return        返回实体的集合
     */
    List<T> queryByExample(Example example);


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
     * 插入一行数据到数据库 并且返回主键
     * @param t
     * @return
     */
    PK insertReturnPk(T t);

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
     * 根据id删除数据
     * @param pk 主键id
     * @return 是否删除成功
     */
    boolean deleteByPrimaryKey(PK pk);

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     * @param t t   实体对象
     * @return      是否删除数据成功
     */
    boolean delete(T t);

    /**
     * 根据id批量删除
     * @param ids id集合
     * @return    是否成功
     */
    boolean deleteByIds(List<PK> ids);


    /**
     * 返回tk-mapper
     * @return {@link BaseTkMapper}
     */
    BaseTkMapper<T, PK> getTkMapper();


}
