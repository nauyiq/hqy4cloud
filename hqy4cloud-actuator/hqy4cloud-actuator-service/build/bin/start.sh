#!/bin/bash

export CUSTOM_SEARCH_NAMES="application,custom"
export CUSTOM_SEARCH_LOCATIONS=file:${BASE_DIR}/conf/


# -XX:+UseSerialGC 年轻代和老年代都用串行收集器
# -XX:+UseParNewGC 年轻代使用 ParNew，老年代使用 Serial Old
# -XX:+UseParallelGC 年轻代使用 ParallerGC，老年代使用 Serial Old
# -XX:+UseParallelOldGC 新生代和老年代都使用并行收集器
# -XX:+UseConcMarkSweepGC，表示年轻代使用 ParNew，老年代的用 CMS
# -XX:+UseG1GC 使用 G1垃圾回收器
# -XX:+UseZGC 使用 ZGC 垃圾回收器


set -x

#堆内存大小
JAVA_OPT="${JAVA_OPT} -Xmx${JVM_XMX} -Xms${JVM_XMS}"

JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
# 虚拟机启动的时候以UTF-8字符集编码来解析class字节码
JAVA_OPT="${JAVA_OPT} -Dfile.encoding=UTF-8"

JAVA_OPT="${JAVA_OPT} -jar ${BASE_DIR}/target/${JAVA_JAR}"

echo "Service is starting, you can docker logs your container."
exec $JAVA ${JAVA_OPT}
