#!/bin/bash
set -x
export CUSTOM_SEARCH_NAMES="application,custom"
export CUSTOM_SEARCH_LOCATIONS=${BASE_DIR}/init.d/,file:${BASE_DIR}/conf/


# -XX:+UseSerialGC 年轻代和老年代都用串行收集器
# -XX:+UseParNewGC 年轻代使用 ParNew，老年代使用 Serial Old
# -XX:+UseParallelGC 年轻代使用 ParallerGC，老年代使用 Serial Old
# -XX:+UseParallelOldGC 新生代和老年代都使用并行收集器
# -XX:+UseConcMarkSweepGC，表示年轻代使用 ParNew，老年代的用 CMS
# -XX:+UseG1GC 使用 G1垃圾回收器
# -XX:+UseZGC 使用 ZGC 垃圾回收器


#堆内存大小
JAVA_OPT="${JAVA_OPT} -Xmx512m -Xms256m"
# 年轻代和年老代比例为1:2
JAVA_OPT="${JAVA_OPT} -XX:NewRatio=2"
# Region size  heapSize/2048 Region size必须是2的指数 取值范围从1M到32M
JAVA_OPT="${JAVA_OPT} -XX:G1HeapRegionSize=4m"
# 元数据空间, 默认为20M
JAVA_OPT="${JAVA_OPT} -XX:MetaspaceSize=40m"
# 最大元数据空间 注意：metaspace太小会引起full gc
JAVA_OPT="${JAVA_OPT} -XX:MaxMetaspaceSize=40m"
# MaxTenuringThreshold设置垃圾的最大年龄. 默认为15 年轻代经历gc进入年老代的年龄
JAVA_OPT="${JAVA_OPT} -XX:MaxTenuringThreshold=10"
# 采用G1垃圾回收器
JAVA_OPT="${JAVA_OPT} -XX:+UseG1GC"
# 当堆内存的使用率达到45%之后就会自动启动G1的并发垃圾回收 默认为45
JAVA_OPT="${JAVA_OPT} -XX:InitiatingHeapOccupancyPercent=45"
# 每次GC最大的停顿毫秒数
JAVA_OPT="${JAVA_OPT} -XX:MaxGCPauseMillis=200"
# gc日志打印 -> 输出详细GC日志；打印gc发生的时间戳；打印gc前后堆栈情况；可以生成更详细的Survivor空间占用日志；定义GC Log 的滚动功能；生成gc文件的数量；gc-log日志大小；gc日志路径
JAVA_OPT="${JAVA_OPT} -XX:+PrintGCDetails"
JAVA_OPT="${JAVA_OPT} -XX:+PrintGCTimeStamps"
JAVA_OPT="${JAVA_OPT} -XX:+PrintHeapAtGC"
JAVA_OPT="${JAVA_OPT} -XX:+PrintAdaptiveSizePolicy"
JAVA_OPT="${JAVA_OPT} -XX:+UseGCLogFileRotation"
JAVA_OPT="${JAVA_OPT} -XX:NumberOfGCLogFiles=4"
JAVA_OPT="${JAVA_OPT} -XX:GCLogFileSize=32m"
JAVA_OPT="${JAVA_OPT} -Xloggc:/home/services/common-collector-service/logs/gc-$(date +%Y%m%d-%H%M).log"
# 发生内存溢出时打印堆栈快照
JAVA_OPT="${JAVA_OPT} -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPT="${JAVA_OPT} -XX:HeapDumpPath=${BASE_DIR}/heap-dump.hprof"
# 虚拟机启动的时候以UTF-8字符集编码来解析class字节码
JAVA_OPT="${JAVA_OPT} -Dfile.encoding=UTF-8"
# 命令用于追加配置文件 原有的application.properties或application.yml文件均有效。
JAVA_OPT="${JAVA_OPT} --spring.config.additional-location=${CUSTOM_SEARCH_LOCATIONS}"

#JAVA_OPT="${JAVA_OPT} --logging.config=${BASE_DIR}/target/logback.xml"
#JAVA_OPT="${JAVA_OPT} --logging.file.path=${BASE_DIR}/logs/"

JAVA_OPT="${JAVA_OPT} -jar ${BASE_DIR}/target/${JAVA_JAR}"


exec $JAVA ${JAVA_OPT}