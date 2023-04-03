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




## AT模式

AT模式需要保证每个业务库，都有一张`undo_log`表，保存着业务数据执行前和执行后的镜像数据。

### 1、前提条件

- 基于支持本地 ACID 事务的关系型数据库。
- Java 应用，通过 JDBC 访问数据库。

### 2、整体机制

两阶段提交协议的演变：

- 一阶段：业务数据和回滚日志记录在同一个本地事务中提交，释放本地锁和连接资源。
- 二阶段：
  - 提交异步化，非常快速地完成。
  - 回滚通过一阶段的回滚日志进行反向补偿。

### 3、读写隔离的实现

#### 写隔离

- 一阶段本地事务提交前，需要确保先拿到 **全局锁** 。
- 拿不到 **全局锁** ，不能提交本地事务。
- 拿 **全局锁** 的尝试被限制在一定范围内，超出范围将放弃，并回滚本地事务，释放本地锁。

#### 读隔离

在数据库本地事务隔离级别 **读已提交（Read Committed）** 或以上的基础上，Seata（AT 模式）的默认全局隔离级别是 **读未提交（Read Uncommitted）** 。

SELECT FOR UPDATE 语句的执行会申请 **全局锁** ，如果 **全局锁** 被其他事务持有，则释放本地锁（回滚 SELECT FOR UPDATE 语句的本地执行）并重试。这个过程中，查询是被 block 住的，直到 **全局锁** 拿到，即读取的相关数据是 **已提交** 的，才返回。

出于总体性能上的考虑，Seata 目前的方案并没有对所有 SELECT 语句都进行代理，仅针对 FOR UPDATE 的 SELECT 语句。

**for update**仅适用于InnoDB，且必须在事务块(BEGIN/COMMIT)中才能生效。在进行事务操作时，通过“for update”语句，MySQL会对查询结果集中每行数据都添加排他锁，其他线程对该记录的更新与删除操作都会阻塞。排他锁包含行锁、表锁。



### TCC 模式

TCC模式也是二阶段提交的模型

- 一阶段 prepare 行为
- 二阶段 commit 或 rollback 行为

并且TCC不依赖底层数据资源的支持 但是对代码的侵入严重

- 一阶段 prepare 行为：调用 **自定义** 的 prepare 逻辑。
- 二阶段 commit 行为：调用 **自定义** 的 commit 逻辑。
- 二阶段 rollback 行为：调用 **自定义** 的 rollback 逻辑。

所谓 TCC 模式，是指支持把 **自定义** 的分支事务纳入到全局事务的管理中。



### #Seata服务端启动命令

seata-server.sh -h 127.0.0.1 -p 8091 -m db -n 1 -e test

    -h: 注册到注册中心的ip
    -p: Server rpc 监听端口
    -m: 全局事务会话信息存储模式，file、db、redis，优先读取启动参数 (Seata-Server 1.3及以上版本支持redis)
    -n: Server node，多个Server时，需区分各自节点，用于生成不同区间的transactionId，以免冲突
    -e: 多环境配置参考 http://seata.io/en-us/docs/ops/multi-configuration-isolation.html
