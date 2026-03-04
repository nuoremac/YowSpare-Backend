package yowyob.comops.api.infrastructure.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import yowyob.comops.api.domain.model.config.AppBusinessSettings;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, AppBusinessSettings> settingsRedisTemplate(ReactiveRedisConnectionFactory factory, ObjectMapper mapper) {
        Jackson2JsonRedisSerializer<AppBusinessSettings> serializer = new Jackson2JsonRedisSerializer<>(mapper, AppBusinessSettings.class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, AppBusinessSettings> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        
        RedisSerializationContext<String, AppBusinessSettings> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    // NOUVEAU BEAN pour stocker des String <-> String
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveStringRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
    }
}