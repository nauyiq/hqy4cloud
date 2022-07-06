package com.hqy.rpc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/19 16:12
 */
@Component
@ConfigurationProperties(prefix = "thrift")
public class ThriftServerProperties {

   /**
    * base multiple count.
    */
   private int multipleBaseCount = 4;

   /**
    * connect rpc service port.
    */
   private int rpcPort = 10001;

   /**
    * thrift service connect failure for retry time.
    */
   private int connectRetryTime = 64;

   /**
    * netty boss group thread number.
    */
   private int nettyBossThreadNum = 1;

   /**
    * netty io worker thread number.
    */
   private int nettyIoWorkerThreadNum = Runtime.getRuntime().availableProcessors() * multipleBaseCount;

   /**
    * netty logic thread number.
    */
   private int nettyLogicThreadNum = Runtime.getRuntime().availableProcessors() * multipleBaseCount;



   public int getMultipleBaseCount() {
      return multipleBaseCount;
   }

   public void setMultipleBaseCount(int multipleBaseCount) {
      this.multipleBaseCount = multipleBaseCount;
   }

   public int getRpcPort() {
      return rpcPort;
   }

   public void setRpcPort(int rpcPort) {
      this.rpcPort = rpcPort;
   }

   public int getConnectRetryTime() {
      return connectRetryTime;
   }

   public void setConnectRetryTime(int connectRetryTime) {
      this.connectRetryTime = connectRetryTime;
   }

   public int getNettyBossThreadNum() {
      return nettyBossThreadNum;
   }

   public void setNettyBossThreadNum(int nettyBossThreadNum) {
      this.nettyBossThreadNum = nettyBossThreadNum;
   }

   public int getNettyIoWorkerThreadNum() {
      return nettyIoWorkerThreadNum;
   }

   public void setNettyIoWorkerThreadNum(int nettyIoWorkerThreadNum) {
      this.nettyIoWorkerThreadNum = nettyIoWorkerThreadNum;
   }

   public int getNettyLogicThreadNum() {
      return nettyLogicThreadNum;
   }

   public void setNettyLogicThreadNum(int nettyLogicThreadNum) {
      this.nettyLogicThreadNum = nettyLogicThreadNum;
   }
}
