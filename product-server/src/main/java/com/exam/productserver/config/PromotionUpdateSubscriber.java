package com.exam.productserver.config;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class PromotionUpdateSubscriber implements MessageListener {
    private final CacheManager cacheManager;
    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        // Message gửi lên Redis chứa product_id
        String productId = new String(
                message.getBody(),
                StandardCharsets.UTF_8
        );

        // Lấy cache sản phẩm
        Cache cache = cacheManager.getCache("products");

        if (cache != null) {
            // Xóa cache theo product_id
            cache.evict(Long.valueOf(productId));

            System.out.println(
                    "Đã xóa cache sản phẩm: " + productId
            );
        }
    }
}
