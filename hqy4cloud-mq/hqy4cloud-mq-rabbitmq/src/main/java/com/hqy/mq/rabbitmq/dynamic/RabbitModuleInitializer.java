package com.hqy.mq.rabbitmq.dynamic;

import cn.hutool.core.convert.Convert;
import com.hqy.mq.rabbitmq.lang.RabbitConstants;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ队列初始化器
 * 实现SmartInitializingSingleton的接口后，当所有单例 bean 都初始化完成以后， Spring的IOC容器会回调该接口的 afterSingletonsInstantiated()方法。
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/31 9:44
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitModuleInitializer implements SmartInitializingSingleton {

    private final AmqpAdmin amqpAdmin;

    private final RabbitModuleProperties rabbitModuleProperties;


    @Override
    public void afterSingletonsInstantiated() {
        log.info("@@@ Dynamic create exchange and queue by configuration.");
        declareRabbitModule();
    }

    /**
     * 声明rabbitmq相关配置
     */
    private void declareRabbitModule() {
        List<RabbitMetadata> rabbitModuleInfos = rabbitModuleProperties.getModules();
        if (CollectionUtils.isEmpty(rabbitModuleInfos)) {
            log.info("@@@ Dynamic created. rabbitModuleInfos is empty.");
            return;
        }

        for (RabbitMetadata rabbitMetadata : rabbitModuleInfos) {
            //校验配置参数
            checkRabbitMetadata(rabbitMetadata);

            //Spring Amqp Queue.
            Queue queue = convertQueue(rabbitMetadata.getQueue());
            //Spring Amqp exchange.
            Exchange exchange = convertExchange(rabbitMetadata.getExchange());
            //Spring binding
            Binding binding = new Binding(queue.getName(), Binding.DestinationType.QUEUE, exchange.getName(), rabbitMetadata.getRootingKey(), null);

            amqpAdmin.declareQueue(queue);
            amqpAdmin.declareExchange(exchange);
            amqpAdmin.declareBinding(binding);
        }


    }

    private Queue convertQueue(RabbitMetadata.Queue queue) {
        Map<String, Object> arguments = queue.getArguments();

        // 转换ttl的类型为long
        if (arguments != null && arguments.containsKey(RabbitConstants.TTL)) {
            arguments.put(RabbitConstants.TTL, Convert.toLong(arguments.get(RabbitConstants.TTL)));
        }

        // 是否需要绑定死信队列
        String deadLetterExchange = queue.getDeadLetterExchange();
        String deadLetterRoutingKey = queue.getDeadLetterRoutingKey();
        if (!StringUtils.isAnyBlank(deadLetterExchange, deadLetterRoutingKey)) {
            if (arguments == null) {
                arguments = new HashMap<>(2);
            }
            arguments.put(RabbitConstants.DEAD_EXCHANGE, deadLetterExchange);
            arguments.put(RabbitConstants.DEAD_EXCHANGE_ROOTING_KEY, deadLetterRoutingKey);
        }
        return new Queue(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), arguments);
    }


    public Exchange convertExchange(RabbitMetadata.Exchange exchangeInfo) {
        AbstractExchange exchange = null;
        //根据不同类型创建交换机
        String name = exchangeInfo.getName();
        ExchangeEnum type = exchangeInfo.getType();
        boolean autoDelete = exchangeInfo.isAutoDelete();
        boolean durable = exchangeInfo.isDurable();
        Map<String, Object> arguments = exchangeInfo.getArguments();

        switch (type) {
            //直连
            case DIRECT:
                exchange = new DirectExchange(name, durable, autoDelete, arguments);
                break;
            case FANOUT:
                exchange = new FanoutExchange(name, durable, autoDelete, arguments);
                break;
            case HEADERS:
                exchange = new HeadersExchange(name, durable, autoDelete, arguments);
                break;
            default:
                exchange = new TopicExchange(name, durable, autoDelete, arguments);
        }

        return exchange;
    }


    private void checkRabbitMetadata(RabbitMetadata rabbitMetadata) {
        String rootingKey = rabbitMetadata.getRootingKey();
        RabbitMetadata.Queue queue = rabbitMetadata.getQueue();
        RabbitMetadata.Exchange exchange = rabbitMetadata.getExchange();
        ExchangeEnum exchangeType = exchange.getType();

        AssertUtil.isFalse(exchangeType != ExchangeEnum.FANOUT && StringUtils.isBlank(rootingKey), "只有交互机类型是FANOUT模式, rootingKey才能为空.");
        AssertUtil.notNull(exchangeType, rootingKey + "绑定的交换机未配置.");
        AssertUtil.notEmpty(exchange.getName(), rootingKey + "绑定交换机名称不能为空.");
        AssertUtil.notNull(queue, rootingKey + "未配置queue.");
        AssertUtil.notEmpty(queue.getName(), rootingKey + "配置的queue.name不能为空.");
    }
}
