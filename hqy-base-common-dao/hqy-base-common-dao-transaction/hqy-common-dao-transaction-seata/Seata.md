# Seata

seata 一款开源的分布式事务解决方案。



## Seata的整体架构

* **TC(Transaction Coordinator) 事务协调者**

  维护全局和分支事务的状态，驱动全局事务提交或回滚。

* **TM(Transaction Manager) 事务管理器**

   定义全局事务的范围：开始全局事务、提交或回滚全局事务。

* **RM（Resource Manager）资源管理器**

  管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。

* **事务回话信息存储**

  非必须角色，理论上可以不引入额外组件。

  事务会话信息存储方式有：file本地文件(不支持HA)，db数据库|redis(支持HA) 但从生产实践角度来看，这个组件也是必须的

* **注册中心**

  非必须角色，理论上可以不引入额外组件。

  默认file，支持file 、nacos 、eureka、redis、zk、consul、etcd3、sofa、custom

* **配置中心**

  非必须角色，理论上可以不引入额外组件。

  默认file，支持file、nacos 、apollo、zk、consul、etcd3、custom

