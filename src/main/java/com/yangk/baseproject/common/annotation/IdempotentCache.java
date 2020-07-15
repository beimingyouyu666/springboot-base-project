package com.yangk.baseproject.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 幂等注解
 * @Author yangkun
 * @Date 2020/6/30
 * @Version 1.0
 * @blame yangkun
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface IdempotentCache {
    /**
    缓存key
     */
    String cacheKey();

    /**
     缓存value数组
     */
    String[] uuidNames() default {};

    /**
     锁过期时间 默认60s
     */
    int lockExpiredTime() default 60;

    /**
     缓存过期时间 默认600s
     */
    int cacheExpired() default 600;

    /**
     获取锁的最大时间 默认1s
     */
    long waitTime() default 1L;

    /**
     是否使用md5对缓存值进行加密
     */
//    boolean useMD5() default true;
}
