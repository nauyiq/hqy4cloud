FROM registry.cn-shenzhen.aliyuncs.com/hqy-parent-all/jdk:1.8
MAINTAINER hongqy<759428167@qq.com>

# set environment
ENV BASE_DIR="/home/services/hqy4cloud-gateway" \
    JAVA_JAR="hqy4cloud-gateway.jar" \
    JAVA_HOME="/usr/local/java/jdk1.8.0_291" \
    JAVA="${JAVA_HOME}/bin/java" \
    JVM_XMS="512m" \
    JVM_XMX="768m" \
    JVM_XMN="256m" \
    JVM_MS="50m" \
    JVM_MMS="100m" \
    TOMCAT_ACCESSLOG_ENABLED="false" \
    TIME_ZONE="Asia/Shanghai"

WORKDIR ${BASE_DIR}

RUN  ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

ADD lib target/lib
ADD ${SERVICE_JAR} target/${SERVICE_JAR}
ADD bin/start.sh bin/start.sh


RUN mkdir -p logs \
    && cd logs \
    && touch sys.out
RUN chmod +x bin/start.sh
RUN chmod +x target/*

EXPOSE 9527

ENTRYPOINT ["bin/start.sh"]
