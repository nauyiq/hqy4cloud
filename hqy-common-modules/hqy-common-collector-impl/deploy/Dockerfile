FROM registry.cn-shenzhen.aliyuncs.com/hqy-parent-all/jdk:1.8.0_291
MAINTAINER hongqy<759428167@qq.com>

ARG SERVICE_DIR="common-collector-service"
ARG SERVICE_JAR="hqy-common-collector-service.jar"

# set environment
ENV BASE_DIR="/home/services/${SERVICE_DIR}" \
    JENKINS_DIR="/home/jenkins/agent/workspace/common-collector-service_dev/hqy-common-modules/hqy-common-collector-impl" \
    JAVA_JAR="${SERVICE_JAR}" \
    JAVA_HOME="/usr/local/java/jdk1.8.0_291" \
    JAVA="${JAVA_HOME}/bin/java" \
    TIME_ZONE="Asia/Shanghai"

RUN mkdir -p ${BASE_DIR}

WORKDIR ${BASE_DIR}
ADD lib target/lib
ADD ${SERVICE_JAR} target/${SERVICE_JAR}
ADD start.sh start.sh

RUN ln -snf /usr/share/zoneinfo/$ /etc/localtime && echo ${TIME_ZONE}> /etc/timezone

RUN mkdir -p logs \
    && cd logs \
    && touch sys.out

RUN chmod +x ${BASE_DIR}/start.sh
RUN chmod 777 target/*


EXPOSE 8888

ENTRYPOINT ["/home/services/common-collector-service/start.sh"]
