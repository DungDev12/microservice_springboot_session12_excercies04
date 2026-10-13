package com.exam.productserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {

    @Bean
    public ChannelTopic promotionUpdatesTopic() {
        return new ChannelTopic("promotion-updates");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            PromotionUpdateSubscriber subscriber,
            ChannelTopic promotionUpdatesTopic) {

        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
                subscriber,
                promotionUpdatesTopic
        );

        return container;
    }
}
