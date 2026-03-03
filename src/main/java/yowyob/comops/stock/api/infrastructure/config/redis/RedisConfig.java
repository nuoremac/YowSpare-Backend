package yowyob.comops.stock.api.infrastructure.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import yowyob.comops.stock.api.infrastructure.security.UserContext;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, UserContext> userContextRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserContext> serializer = new Jackson2JsonRedisSerializer<>(UserContext.class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, UserContext> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        
        RedisSerializationContext<String, UserContext> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}