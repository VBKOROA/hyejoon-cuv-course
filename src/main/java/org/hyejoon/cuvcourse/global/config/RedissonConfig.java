package org.hyejoon.cuvcourse.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
public class RedissonConfig {

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient(@Value("${spring.data.redis.host}") String redistHost,
        @Value("${spring.data.redis.port}") int redisPort, ObjectMapper objectMapper) {
        Config redissonConfig = new Config();

        redissonConfig.useSingleServer().setAddress(REDISSON_HOST_PREFIX + redistHost + ":"
            + redisPort);

        return Redisson.create(redissonConfig);
    }
}
