package com.yangk.baseproject.common.aspect;

import com.alibaba.fastjson.JSON;
import com.yangk.baseproject.common.annotation.IdempotentCache;
import com.yangk.baseproject.common.constant.RedisConstant;
import com.yangk.baseproject.common.enums.EnumCommomSysErrorCode;
import com.yangk.baseproject.common.exception.BusinessRuntimeException;
import com.yangk.baseproject.common.redis.RedisUtil;
import com.yangk.baseproject.common.util.RedissonUtil;
import com.yangk.baseproject.domain.response.ResponseMsg;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description 幂等注解切面
 * @Author yangkun
 * @Date 2020/6/30
 * @Version 1.0
 * @blame yangkun
 */
@Aspect
@Configuration
public class IdempotentCacheAspect {
    private static final Logger log = LoggerFactory.getLogger(IdempotentCacheAspect.class);

    public static final String RESULT = "result";

//    @Autowired
//    private JedisClusterUtil jedisClusterUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonUtil redissonUtil;

    @Pointcut("@annotation(com.yangk.baseproject.common.annotation.IdempotentCache)")
    public void idempotentCacheAnnotationPointcut() {
    }

    @Around("idempotentCacheAnnotationPointcut()")
    public Object invokeResourceWithAnnotation(ProceedingJoinPoint pjp) throws Throwable {
        // 获取方法签名对象
        Method originMethod = getMethod(pjp);
        // 获取注解
        IdempotentCache annotation = originMethod.getAnnotation(IdempotentCache.class);
        // 获取要缓存的值
        String[] uuidNames = annotation.uuidNames();
//        String uuid = getUuidValue(pjp, uuidNames, annotation.useMD5());
        String uuid = getUuidValue(pjp, uuidNames);
        // 获取返回值类型
        Class<?> returnType = originMethod.getReturnType();
        // 对请求参数进行缓存
        return cacheUuid(pjp, uuid, annotation, returnType);
    }

    /**
    * @Description:
     *  方法参数为对象时，要指定uuidName的属性字段，通过反射从对象中获取属性值；
     *  方法参数为基本类型，不用指定uuidName，直接取第一个参数值
    * @Author: yangkun
    * @Date: 2020/6/30
    * @Param:
     * @param pjp
     * @param uuidNames
     * @param *useMD5
    * @return: java.lang.String
    */
    private String getUuidValue(ProceedingJoinPoint pjp, String[] uuidNames/*, boolean useMD5*/) throws IllegalAccessException, InvocationTargetException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String uuid = "";

        // 1、使用注解时未显示写明缓存value是哪个字段，则取参数列表中第一个参数
        if (uuidNames.length == 0) {
            // 获取参数列表
            Object[] args = pjp.getArgs();
            if (args != null && args.length > 0) {
//                if (useMD5) {
//                    // 使用md5加密，则将缓存value转换为十六进制
//                    uuid = MD5Util.encode2hex(args[0].toString());
//                } else {
//                    uuid = (String) args[0];
//                }
                uuid = (String) args[0];
            }
            return uuid;
        }

        // 2、写明了缓存uuidName属性名，则通过反射从第一个参数对象中获取到属性值
        // 取第一个参数的Class对象
        Object argFirst = pjp.getArgs()[0];
        Class<?> objClazz = argFirst.getClass();
        // 第一个参数类的属性列表
        Field[] declaredFields = objClazz.getDeclaredFields();
        // 取第一个参数的Class对象方法列表
        Method[] methods = objClazz.getMethods();
        // 遍历待缓存的value数组
        for (String uuidName : uuidNames) {
            for (Method method : methods) {
                // 通过反射调用get方法获取uuidName
                if (("get" + uuidName).equalsIgnoreCase(method.getName())) {
                    String tmp;
                    Object obj = method.invoke(pjp.getArgs()[0], new Object[0]);
                    if (obj instanceof Date) {
                        tmp = String.valueOf(((Date) obj).getTime());
                    } else {
                        tmp = obj.toString();
                        if (StringUtils.isEmpty(tmp)) {
                            throw new IllegalStateException(uuidName + " cannot be null or empty");
                        }
                    }
                    uuid = uuid + tmp;
                }
            }
//            for (Field declaredField : declaredFields) {
//                String tmp;
//                if (StringUtils.equals(declaredField.getName(),uuidName)) {
//                    Object o = declaredField.get(argFirst);
//                    if (o instanceof Date) {
//                        tmp = String.valueOf(((Date) o).getTime());
//                    } else {
//                        tmp = o.toString();
//                        if (StringUtils.isEmpty(tmp)) {
//                            throw new IllegalStateException(uuidName + " cannot be null or empty");
//                        }
//                    }
//                    uuid = uuid + tmp;
//                }
//            }
        }
        return uuid;
    }

    /**
    * @Description:  对请求参数进行缓存
    * @Author: yangkun
    * @Date: 2020/6/30
    * @Param:
     * @param pjp
     * @param orderUuid 使用注解时指定的uuidName
     * @param annotation
     * @param returnType 使用注解的方法返回值类型
    * @return: java.lang.Object
    */
    private Object cacheUuid(ProceedingJoinPoint pjp, String orderUuid, IdempotentCache annotation,
                             Class<?> returnType) throws Throwable {
        // 此次请求线程id
        String threadRequestUuid = UUID.randomUUID().toString();
        // 分布式锁 key
        String lockKey = RedisConstant.KEY_PREFIX + annotation.cacheKey() + orderUuid;
        // 缓存key -- 注解上指定的key+uuidName
        String resultKey = RedisConstant.KEY_PREFIX + annotation.cacheKey() + orderUuid + RESULT;
        try {
            // 看redis是否已存在缓存
//            String cacheResult = jedisClusterUtil.get(resultKey);
            String cacheResult = (String) redisUtil.get(resultKey);
            if (StringUtils.isNotEmpty(cacheResult)) {
                log.debug("debug 幂等拦截，将缓存的响应返回，cacheResult:{}",cacheResult);
                log.info("幂等拦截，将缓存的响应返回，cacheResult:{}",cacheResult);
                // 如果已存在，反序列化为对象返回
                return JSON.parseObject(cacheResult, returnType);
            }
            // 获取分布式锁，根据超时时间，未获取锁则不停重试
            boolean lockFlag = redissonUtil.lock(lockKey,Long.valueOf(annotation.waitTime()), Long.valueOf(annotation.lockExpiredTime()), TimeUnit.SECONDS);
//            boolean lockFlag = this.jedisClusterUtil.tryLock(lockKey, threadRequestUuid,
//                    Long.valueOf(annotation.waitTime()), Integer.valueOf(annotation.lockExpiredTime()));
            // 获取锁成功
            if (lockFlag) {
                // 看redis是否已存在缓存
//                cacheResult = jedisClusterUtil.get(resultKey);
                cacheResult = (String) redisUtil.get(resultKey);
                if (StringUtils.isNotEmpty(cacheResult)) {
                    return JSON.parseObject(cacheResult, returnType);
                }
                // 如果不存在则执行目标方法，将方法返回值json序列化保存在缓存中
                Object result = pjp.proceed();
//                jedisClusterUtil.set(resultKey, JSON.toJSONString(result), annotation.cacheExpired());
                redisUtil.set(resultKey, JSON.toJSONString(result), Long.valueOf(annotation.cacheExpired()));
                redissonUtil.unlock(lockKey);
                return result;
            }
            // 获取锁失败，未设置获取锁超时时间，抛异常“请求正在执行”
            if (annotation.waitTime() == 0L) {
                return ResponseMsg.buildFailMsg(EnumCommomSysErrorCode.REQUEST_IS_BEING_EXECUTED.name(), "The request" +
                        " is being executed, please try again later!");
            }
            throw BusinessRuntimeException.buildBusinessRuntimeException(EnumCommomSysErrorCode.WAIT_TIMEOUT_ERROR,
                    lockKey);
        } catch (BusinessRuntimeException e) {
            if (ResponseMsg.class == returnType) {
                log.error("\nmsg: {}  ", new Object[]{e.getMessage(), e});
                return ResponseMsg.buildFailMsg(e);
            }
            throw e;
        } catch (Exception ex) {
            if (ResponseMsg.class == returnType) {
                log.error("{}", ex);
                return ResponseMsg.buildUnknownFailMsg();
            }
            throw ex;
        } finally {
            // 释放锁
//            this.jedisClusterUtil.unLock(lockKey, threadRequestUuid);
        }
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}

