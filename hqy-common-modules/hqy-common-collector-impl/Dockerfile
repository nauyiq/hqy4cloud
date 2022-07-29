FROM java:8
COPY target/lib /usr/local/service
COPY target/hqy-common-collector-service.jar /usr/local/service

ENTRYPOINT ["java","-jar","hqy-common-collector-service.jar"]

EXPOSE 8888