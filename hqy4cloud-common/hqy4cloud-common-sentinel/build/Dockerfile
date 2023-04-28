FROM openjdk:11.0.3-jdk-stretch

MAINTAINER 759428167@qq.com

ARG version
ARG port

# sentinel version
ENV SENTINEL_VERSION ${version:-1.8.5}
#PORT
ENV PORT ${port:-8989}
ENV JAVA_OPT=""
#
ENV PROJECT_NAME sentinel-dashboard
ENV SERVER_HOST localhost
ENV SERVER_PORT 8989
ENV USERNAME sentinel
ENV PASSWORD sentinel

# sentinel home
ENV SENTINEL_HOME  /opt/
ENV SENTINEL_LOGS  /opt/logs

#tme zone
RUN rm -rf /etc/localtime \
&& ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# create logs
RUN mkdir -p ${SENTINEL_LOGS}
COPY sentinel-dashboard.jar ${SENTINEL_HOME}
COPY bin/* /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh \
&& ln -s /usr/local/bin/docker-entrypoint.sh /opt/docker-entrypoint.sh

VOLUME ${SENTINEL_LOGS}

WORKDIR  ${SENTINEL_HOME}

EXPOSE ${PORT} 8719

CMD java ${JAVA_OPT} -jar sentinel-dashboard.jar

ENTRYPOINT ["docker-entrypoint.sh"]