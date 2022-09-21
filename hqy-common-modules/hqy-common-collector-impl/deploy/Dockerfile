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

RUN mkdir -p ${BASE_DIR} && ls &{JENKINS_DIR}


# ADD ../target/classes/logback.xml target/logback.xml
ADD ../target/lib ${BASE_DIR}/target/lib
ADD ../target/${SERVICE_JAR} ${BASE_DIR}/target/${SERVICE_JAR}
ADD g1_start.sh ${BASE_DIR}/tg1_start.sh
WORKDIR ${BASE_DIR}
RUN ln -snf /usr/share/zoneinfo/$ /etc/localtime && echo ${TIME_ZONE}> /etc/timezone

RUN mkdir -p init.d

RUN mkdir -p logs \
    && cd logs \
    && touch sys.out

RUN chmod +x g1_start.sh
RUN chmod 777 target/*


EXPOSE 8888

ENTRYPOINT ["g1_start.sh"]