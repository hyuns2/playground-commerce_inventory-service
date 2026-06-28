package io.playground.inventoryservice.infrastructure.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {
    @Bean
    public RedisScript<Long> stockScript() {
        return RedisScript.of(
                new ClassPathResource("scripts/stock.lua"),
                Long.class
        );
    }
}
