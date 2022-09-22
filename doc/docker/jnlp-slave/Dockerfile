FROM jenkins/jnlp-slave:latest
MAINTAINER hongqy<759428167@qq.com>

# 安装 maven
COPY apache-maven-3.8.6-bin.tar.gz .

RUN tar -zxf apache-maven-3.8.6-bin.tar.gz && \
    mv apache-maven-3.8.6 /usr/local && \
    rm -f apache-maven-3.8.6-bin.tar.gz && \
    ln -s /usr/local/apache-maven-3.8.6/bin/mvn /usr/bin/mvn && \
    mkdir -p /usr/local/apache-maven-3.8.6/repo

COPY settings.xml /usr/local/apache-maven-3.8.6/conf/settings.xml



