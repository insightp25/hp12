package io.clean.tdd.hp12.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisConnectionFactory redisLockConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, String> redisLockTemplate(RedisConnectionFactory redisLockConnectionFactory) {
        RedisTemplate<String, String> redisLockTemplate = new RedisTemplate<>();
        redisLockTemplate.setConnectionFactory(redisLockConnectionFactory);
        redisLockTemplate.setKeySerializer(new StringRedisSerializer());
        redisLockTemplate.setValueSerializer(new StringRedisSerializer());
        return redisLockTemplate;
    }
}
