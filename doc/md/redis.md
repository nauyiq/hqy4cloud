# redis

## 1.简介

Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。 它支持多种类型的数据结构，如 字符串（strings）， 散列（hashes）， 列表（lists）， 集合（sets）， 有序集合（sorted sets） 与范围查询， bitmaps， hyperloglogs 和 地理空间（geospatial） 索引半径查询。 Redis 内置了 复制（replication），LUA脚本（Lua scripting）， LRU驱动事件（LRU eviction），事务（transactions） 和不同级别的 磁盘持久化（persistence）， 并通过 Redis哨兵（Sentinel）和自动 分区（Cluster）提供高可用性（high availability）。



## 2.常见命令

### redis-key

<http://www.redis.cn/commands.html>

- keys *  			     查看所有的key
- flushdb 			     清除当前数据库
- flushall	 		     清除所有数据库
- exists key 		     判断当前key是否存在
- expire key seconds   设置key的过期时间
- ttl key			     查看过期时间



### String

- APPEND key value             在原有字符串后面追加
- setex key seconds value  设置过期时间
- setnx key values 	             不存在设置 在分布式锁中常常使用
- mset   			             同时设置多个值
- mget 			             同时获取多个值
- msetnx			             msetnx是原子性的操作~
- getset   key value              组合命令 先get后set



## 4.redis集群

### 主从复制

`主从复制`是指将一台redis服务器的数据，复制到其他redis服务器。前者称为主节点（master/leader），后者称为从节点（slave/follower）；数据的复制是单向的，只能由主节点到从节点。master以写为主，slave以读为主。

#### **命令**

SLAVEOF host port 

info replication

#### **配置文件**

replicaof 127.0.0.1 6379 选择master

masterauth  #master验证密码

#### 原理

Slave启动成功连接到master后会发送一个sync同步命令

master接收到命令，启动后台的存盘进程，同时收集所有接收到用于修改数据集的命令，在后台进程执行完毕之后，master讲传送整个文件到slave，并完成一次完全同步。

- 全量复制：slave服务在接收到数据库文件数据后，将其存盘并加载到内存中
- 增量复制：master继续将新的所有收集到的修改命令一次传给slave，完成同步但是只要重新连接master，一次完全同步（全量复制）将被自动执行！主机上的数据一定可以在从机中看到

如果主机断开了 可以使用 slaveof no one 让自己变成主机 (手动)



### 哨兵模式







