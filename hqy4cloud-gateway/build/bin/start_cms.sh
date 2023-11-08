#!/bin/bash
export CUSTOM_SEARCH_NAMES="application,custom"
export CUSTOM_SEARCH_LOCATIONS=file:${BASE_DIR}/conf/

set -x

#堆内存大小
JAVA_OPT="${JAVA_OPT} -Xmx${JVM_XMX} -Xms${JVM_XMS}"
# 新生代采用ParNewGC（并行垃圾回收器）
JAVA_OPT="${JAVA_OPT} -XX:+UseParNewGC"
# 老年代采用CMS垃圾回收器
JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC"
# 并行运行最终标记阶段，加快最终标记的速度
JAVA_OPT="${JAVA_OPT} -XX:+CMSParallelRemarkEnabled"
# 经过几次CMS Full GC的时候整理一次碎片
JAVA_OPTS="${JAVA_OPTS} -XX:CMSFullGCsBeforeCompaction=5"
# 在进行Full GC时对内存进行压缩
JAVA_OPTS="${JAVA_OPTS} -XX:+UseCMSCompactAtFullCollection"
# 设置触发CMS老年代回收的内存使用率占比
JAVA_OPTS="${JAVA_OPTS} -XX:CMSInitiatingOccupancyFraction=80"
# gc日志打印
JAVA_OPT="${JAVA_OPT} -Xloggc:${BASE_DIR}/logs/gc-$(date +%Y%m%d-%H%M).log"
JAVA_OPT="${JAVA_OPT} -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -XX:+PrintGCTimeStamps -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
# 发生内存溢出时打印堆栈快照
JAVA_OPT="${JAVA_OPT} -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPT="${JAVA_OPT} -XX:HeapDumpPath=${BASE_DIR}/heap-dump.hprof"
# 虚拟机启动的时候以UTF-8字符集编码来解析class字节码
JAVA_OPT="${JAVA_OPT} -Dfile.encoding=UTF-8"

# 可继续拓展JVM参数
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
JAVA_OPT="${JAVA_OPT} -jar ${BASE_DIR}/target/${JAVA_JAR}"

# 启动java服务
echo "Service is starting, you can docker logs your container."
exec $JAVA ${JAVA_OPT}