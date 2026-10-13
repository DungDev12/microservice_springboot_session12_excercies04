package com.exam.promotionservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionPublisher {
    private static final String CHANNEL = "promotion-updates";

    private final StringRedisTemplate redisTemplate;

    public void publishPromotionUpdate(Long productId) {
        redisTemplate.convertAndSend(
                CHANNEL,
                productId.toString()
        );
    }

}
