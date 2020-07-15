package com.yangk.baseproject.common.redis;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yangk.baseproject.common.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.DigestUtils;

import java.time.Duration;

/**
 * @Description todo 这里配置，统一设置过期时间不生效
 * @Author yangkun
 * @Date 2020/4/9
 * @Version 1.0
 * @blame yangkun
 */
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig  extends CachingConfigurerSupport {

    /**
     * 包路径与方法间隔符
     */
    private static final String CACHE_MOTHOD_SEPARATOR = ".";

    /**
     * 方法与参数的间隔符
     */
    private static final String CACHE_PARAM_SEPARATOR = "#";

    /**
     * 默认 redis key 生成模式
     * 方法全路径#MD5(参数)，如：com.yangk.ironforge.xms.controller.CustomerAccountController.findCustomerSurplus#305fd498c7f6abf3c2643f0524d20002
     *
     * 注意: 该方法只是声明了key的生成策略,还未被使用,需在@Cacheable注解中指定keyGenerator
     *   如: @Cacheable(value = "key", keyGenerator = "cacheKeyGenerator")
     */
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder()
                    .append(RedisConstant.KEY_PREFIX)
                    .append(target.getClass().getName())
                    .append(CACHE_MOTHOD_SEPARATOR)
                    .append(method.getName())
                    .append(CACHE_PARAM_SEPARATOR);
            // 转为 json字符串
            String json = JSON.toJSONString(params);

            // 把请求参数转为 MD5
            String paramsMd5 = DigestUtils.md5DigestAsHex(json.getBytes());
            sb.append(paramsMd5);

            String key = sb.toString();
            log.debug("Generate key:{}", key);
            return key;
        };
    }

    /**
    * @Description:  redis全局默认配置
    * @Author: yangkun
    * @Date: 2020/6/28
    * @Param:
     * @param redisConnectionFactory
    * @return: org.springframework.cache.CacheManager
    */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 定义key序列化方式
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        // 定义value的序列化方式
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10L))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues()
                .prefixKeysWith(RedisConstant.KEY_PREFIX);

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        // 定义value的序列化方式
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

}
