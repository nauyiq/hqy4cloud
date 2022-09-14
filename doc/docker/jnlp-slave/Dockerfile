FROM jenkins/jnlp-slave:latest
MAINTAINER hongqy<759428167@qq.com>

LABEL Description="This is a extend image base from jenkins/jnlp-slave which install maven and git in it."

#install maven
ADD apache-maven-3.8.6-bin.tar.gz /usr/lib/
ENV M2_HOME /usr/lib/apache-maven-3.8.6
ENV PATH $JAVA_HOME/bin:$JRE_HOME/bin:$M2_HOME/bin:$PATH
RUN ln -s /usr/lib/apache-maven-3.8.6/bin/mvn /usr/bin/mvn
