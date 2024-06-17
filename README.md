# hqy4cloud

这是一个致力于整合各类技术的Java框架。

> 本框架开源、免费，主要用于学习各技术使用。

**如果有任何问题，可以通过以下方式联系我（但是可能不会及时响应，请理解）**

- qq: 759428167
- email：hongqy1024@163.com

## 项目结构

``` 
hqy4cloud
  ├── hqy4cloud-actuator -- 集成了springboot actuator功能，提供了服务治理的相关actuator方案。
  ├── hqy4cloud-auth -- 提供了spring security的oauth账号认证服务以及框架层面的数据管理后台。
  ├── hqy4cloud-base-service -- 提供了框架层面的基础服务，例如数据采集、通讯报警、分布式ID等服务
  ├── hqy4cloud-common -- 将各类技术进行整合，例如db、sentinel、canal等。
  ├── hqy4cloud-gateway -- spring cloud gateway服务
  ├── hqy4cloud-mq -- 提供了mq服务的集成，包括rabbitmq、rocketmq、kafka
  ├── hqy4cloud-netty -- netty框架集成、netty实现的部分协议比如socketIO、websocket、mqtt等。
  ├── hqy4cloud--registry 注册中心模块，抽象出服务的注册与发现
  ├── hqy4cloud--rpc 提供了rpc相应的功能，包括rpc的监控、rpc负载均衡、以及thriftrpc的实现。
```

