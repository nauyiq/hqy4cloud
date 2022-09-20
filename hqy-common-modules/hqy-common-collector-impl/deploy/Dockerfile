FROM registry.cn-shenzhen.aliyuncs.com/hqy-parent-all/jdk:1.8.0_291
MAINTAINER hongqy<759428167@qq.com>

ARG SERVICE_DIR="common-collector-service"
ARG SERVICE_JAR="hqy-common-collector-service.jar"

# set environment
ENV BASE_DIR="/home/services/${SERVICE_DIR}" \
    JAVA_JAR="${SERVICE_JAR}" \
    JAVA_HOME="/usr/local/java/jdk1.8.0_291" \
    JAVA="${JAVA_HOME}/bin/java" \
    TIME_ZONE="Asia/Shanghai"

WORKDIR ${BASE_DIR}
# ADD ../target/classes/logback.xml target/logback.xml
ADD ../target/lib target/lib
ADD ../target/${SERVICE_JAR} target/${SERVICE_JAR}
RUN ln -snf /usr/share/zoneinfo/$ /etc/localtime && echo ${TIME_ZONE}> /etc/timezone

ADD g1_start.sh g1_start.sh
RUN mkdir -p init.d

RUN mkdir -p logs \
    && cd logs \
    && touch sys.out

RUN chmod +x g1_start.sh
RUN chmod 777 target/*


EXPOSE 8888

ENTRYPOINT ["g1_start.sh"]