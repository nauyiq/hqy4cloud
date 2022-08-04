#!/bin/bash
set -x
export CUSTOM_SEARCH_NAMES="application,custom"
export CUSTOM_SEARCH_LOCATIONS=${BASE_DIR}/init.d/,file:${BASE_DIR}/conf/

JAVA_OPT="${JAVA_OPT} -Dsimple_ecommerce.home=${BASE_DIR}"
JAVA_OPT="${JAVA_OPT} -jar ${BASE_DIR}/target/${SERVICE_JAR}"
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
JAVA_OPT="${JAVA_OPT} --spring.config.additional-location=${CUSTOM_SEARCH_LOCATIONS}"
JAVA_OPT="${JAVA_OPT} --spring.config.name=${CUSTOM_SEARCH_NAMES}"
