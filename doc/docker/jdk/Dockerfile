FROM centos:7
MAINTAINER hongqy<759428167@qq.com>
WORKDIR /usr/local/java

ADD jdk-8u291-linux-x64.tar.gz /usr/local/java

ENV JAVA_HOME=/usr/local/java/jdk1.8.0_291
ENV CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH=$JAVA_HOME/bin:$PATH


