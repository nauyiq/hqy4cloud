FROM registry.cn-shenzhen.aliyuncs.com/hqy-parent-all/jdk:17
MAINTAINER hongqy<759428167@qq.com>

# set environment
ENV BASE_DIR="/hqy4cloud/hqy4cloud-gateway" \
    JAVA_JAR="hqy4cloud-gateway.jar" \
    JAVA_HOME="/usr/local/java/jdk-17.0.9" \
    JAVA="${JAVA_HOME}/bin/java" \
    JVM_XMS="512m" \
    JVM_XMX="512m" \
    JVM_XMN="256m" \
    JVM_MS="50m" \
    JVM_MMS="100m" \
    TOMCAT_ACCESSLOG_ENABLED="false" \
    TIME_ZONE="Asia/Shanghai" \
    START_SH="bin/start_g1.sh" \
    JAVA_OPT_EXT=""

WORKDIR ${BASE_DIR}

RUN ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

ADD lib target/lib
ADD ${SERVICE_JAR} target/${SERVICE_JAR}
ADD bin/start_g1.sh bin/start_g1.sh
ADD bin/start_cms.sh bin/start_cms.sh

RUN mkdir -p logs \
    && chmod +x bin/* \
    && chmod +x target/*

EXPOSE 9527

ENTRYPOINT ["bin/start_g1.sh"]
