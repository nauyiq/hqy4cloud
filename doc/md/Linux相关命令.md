## Linux相关命令



**查看进程连接数**

``ls /proc/pid/fd -l | grep socket: | wc -l``

**查看端口占用情况**

``netstat -ntlp``

**根据进程号查看端口**

``netstat -nap|grep pid``

**根据端口查看socket连接数**

``netstat -n |grep 9097 | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'``

**启动rocketmq-console**

``nohup  java -jar rocketmq-console-ng-1.0.0.jar --rocketmq.config.namesrvAddr='localhost:9876' &``

**Docker上安装skywalking **

```
# skywalking-oap

docker run \
--name skywalking-oap \
--restart always \
-p 11800:11800 \
-p 12800:12800 -d \
--privileged=true \
-e TZ=Asia/Shanghai \
-e SW_STORAGE=elasticsearch7 \
-e SW_STORAGE_ES_CLUSTER_NODES=47.106.168.100:9200 \
-v /etc/localtime:/etc/localtime:ro \
apache/skywalking-oap-server:8.6.0-es7

# skywalking-ui
docker run \
--name skywalking-ui \
--restart always \
-p 8081:8080 -d \
--privileged=true \
--link skywalking-oap:skywalking-oap \
-e TZ=Asia/Shanghai \
-e SW_OAP_ADDRESS=47.106.168.100:12800 \
-v /etc/localtime:/etc/localtime:ro \
apache/skywalking-ui:8.6.0

#idea启动服务
-javaagent:C:\Users\AD04\Desktop\apache-skywalking-apm-bin\agent\skywalking-agent.jar -DSW_AGENT_NAME=accouner-auth -DSW_AGENT_COLLECTOR_BACKEND_SERVICES=47.106.168.100:11800

```

**Docker上安装elasticsearch和kibana**

```
# elasticsearch-server
docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
-e "discovery.type=single-node" \
-e ES_JAVA_OPTS="-Xms128m -Xmx256m" \
-v /usr/local/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /usr/local/elasticsearch/data:/usr/share/elasticsearch/data \
-v /usr/local/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-d elasticsearch:7.14.0


# kibana

vim /usr/local/kibana/config/kibana.yml

配置以下内容：

server.name: kibana

server.host: "0"

elasticsearch.hosts: [ "http://192.168.22.33:9200" ]

xpack.monitoring.ui.container.elasticsearch.enabled: true

(192.168.22.33为自己的虚拟机ip地址，根据自己的地址进行修改。）

docker run -d --name=kibana --restart=always -p 5601:5601 -v /usr/local/kibana/config/kibana.yml:/usr/share/kibana/config/kibana.yml kibana:7.14.0




```



