#!/bin/bash
set -x
export CUSTOM_SEARCH_NAMES="application,custom"
export CUSTOM_SEARCH_LOCATIONS=${BASE_DIR}/init.d/,file:${BASE_DIR}/conf/

JAVA_OPT="${JAVA_OPT} -Dsimple_ecommerce.home=${BASE_DIR}"
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
JAVA_OPT="${JAVA_OPT} -Xloggc:/home/services/common-collector-service/logs/gc-$(date +%Y%m%d-%H%M).log"
JAVA_OPT="${JAVA_OPT} -jar ${BASE_DIR}/target/hqy-common-collector-service.jar"
JAVA_OPT="${JAVA_OPT} --spring.config.additional-location=${CUSTOM_SEARCH_LOCATIONS}"
JAVA_OPT="${JAVA_OPT} --spring.config.name=${CUSTOM_SEARCH_NAMES}"
JAVA_OPT="${JAVA_OPT} --logging.config=${BASE_DIR}/target/logback.xml"
JAVA_OPT="${JAVA_OPT} --logging.file.path=${BASE_DIR}/logs/"
JAVA_OPT="${JAVA_OPT} --server.max-http-header-size=524288"


echo "service is starting, you can docker logs your container"
exec $JAVA ${JAVA_OPT}