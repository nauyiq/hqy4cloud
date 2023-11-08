FROM openjdk:8-jdk-alpine
MAINTAINER 759428167@qq.com

ENV  SENTINEL_VERSION="1.8.5" \
    PORT="8080" \
    SERVER_PORT="8719" \
    PROJECT_NAME="sentinel-dashboard" \
    USERNAME="sentinel" \
    PASSWORD="sentinel" \
    SENTINEL_HOME="/home/services/sentinel" \
    SENTINEL_LOGS="/home/services/sentinel/logs" \
    TIME_ZONE="Asia/Shanghai"

WORKDIR ${SENTINEL_HOME}

RUN  ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

ADD https://github.com/alibaba/Sentinel/releases/download/${SENTINEL_VERSION}/sentinel-dashboard-${SENTINEL_VERSION}.jar ./sentinel-dashboard.jar

# create logs
RUN mkdir -p ${SENTINEL_LOGS}

EXPOSE ${PORT} ${SERVER_PORT}

# java启动jar包
ENTRYPOINT ["java", "-Dserver.port=${PORT}" , "-Dproject.name=${PROJECT_NAME}" ,"-jar","sentinel-dashboard.jar"]
