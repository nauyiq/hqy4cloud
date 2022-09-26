#!/bin/bash
set -x

# -XX:+UseSerialGC 年轻代和老年代都用串行收集器
# -XX:+UseParNewGC 年轻代使用 ParNew，老年代使用 Serial Old
# -XX:+UseParallelGC 年轻代使用 ParallerGC，老年代使用 Serial Old
# -XX:+UseParallelOldGC 新生代和老年代都使用并行收集器
# -XX:+UseConcMarkSweepGC，表示年轻代使用 ParNew，老年代的用 CMS
# -XX:+UseG1GC 使用 G1垃圾回收器
# -XX:+UseZGC 使用 ZGC 垃圾回收器


#堆内存大小
JAVA_OPT="${JAVA_OPT} -Xmx1024m -Xms512m"
# 年轻代和年老代比例为1:2 G1垃圾回收期 如果指定NewRatio 或-Xmn参数 则G1 设定的这个目标 即MaxGCPauseMillis失效
#JAVA_OPT="${JAVA_OPT} -XX:NewRatio=2"
# 采用G1垃圾回收器
JAVA_OPT="${JAVA_OPT} -XX:+UseG1GC"
# Region size  heapSize/2048 Region size必须是2的指数 取值范围从1M到32M
JAVA_OPT="${JAVA_OPT} -XX:G1HeapRegionSize=2m"
JAVA_OPT="${JAVA_OPT} -XX:+ParallelRefProcEnabled"
# 用于控制对象经过GC多少次仍然存活后晋升到老年代的最大阈值 默认15
JAVA_OPT="${JAVA_OPT} -XX:MaxTenuringThreshold=10"
# 元数据空间, 默认为20M
JAVA_OPT="${JAVA_OPT} -XX:MetaspaceSize=100m"
# 最大元数据空间 注意：metaspace太小会引起full gc
JAVA_OPT="${JAVA_OPT} -XX:MaxMetaspaceSize=100m"
# 最大的可使用的直接内存
JAVA_OPT="${JAVA_OPT} -XX:MaxDirectMemorySize=40M"
# MaxTenuringThreshold设置垃圾的最大年龄. 默认为15 年轻代经历gc进入年老代的年龄
JAVA_OPT="${JAVA_OPT} -XX:MaxTenuringThreshold=10"
# 服务启动的时候真实的分配物理内存给jvm
JAVA_OPT="${JAVA_OPT} -XX:+AlwaysPreTouch"

# 当堆内存的使用率达到45%之后就会自动启动G1的并发垃圾回收 默认为45
JAVA_OPT="${JAVA_OPT} -XX:InitiatingHeapOccupancyPercent=45"
# 每次GC最大的停顿毫秒数
JAVA_OPT="${JAVA_OPT} -XX:MaxGCPauseMillis=200"

# gc日志打印
JAVA_OPT="${JAVA_OPT} -verbose:gc"
JAVA_OPT="${JAVA_OPT} -XX:+PrintGCDetails"
JAVA_OPT="${JAVA_OPT} -XX:+PrintGCDateStamps"
JAVA_OPT="${JAVA_OPT} -XX:+PrintHeapAtGC"
JAVA_OPT="${JAVA_OPT} -XX:+PrintGCApplicationStoppedTime"
JAVA_OPT="${JAVA_OPT} -XX:+PrintGCApplicationConcurrentTime"
JAVA_OPT="${JAVA_OPT} -XX:+PrintTenuringDistribution"
JAVA_OPT="${JAVA_OPT} -XX:+PrintClassHistogramBeforeFullGC"
JAVA_OPT="${JAVA_OPT} -XX:+PrintClassHistogramAfterFullGC"
JAVA_OPT="${JAVA_OPT} -Xloggc:/home/services/cloud-gateway-service/logs/gc-$(date +%Y%m%d-%H%M).log"
# 发生内存溢出时打印堆栈快照
JAVA_OPT="${JAVA_OPT} -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPT="${JAVA_OPT} -XX:HeapDumpPath=${BASE_DIR}/heap-dump.hprof"
# 虚拟机启动的时候以UTF-8字符集编码来解析class字节码
JAVA_OPT="${JAVA_OPT} -Dfile.encoding=UTF-8"


ls ${BASE_DIR}/target/
ls ${BASE_DIR}/target/lib

JAVA_OPT="${JAVA_OPT} -jar ${BASE_DIR}/target/${JAVA_JAR}"


exec $JAVA ${JAVA_OPT}