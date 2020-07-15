package com.yangk.baseproject.common.aspect;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description service层性能监控
 * @Author yangkun
 * @Date 2020/6/23
 * @Version 1.0
 * @blame yangkun
 */
@Aspect
@Configuration
public class EfficiencyMonitorAspect {

    private static final Logger logger = LoggerFactory.getLogger(EfficiencyMonitorAspect.class);
    
    @Pointcut("execution(* com.yangk.baseproject.service.impl.*.*(..))")
    public void excudeService() {
    }
    
    @SuppressWarnings("rawtypes")
    @Around("excudeService()")  
    public Object doAroundAdvice(ProceedingJoinPoint point) throws Throwable {  
        long start = System.currentTimeMillis();
        String desc = "成功";
        Object ret = null;
        try {  
            ret = point.proceed(); 
        } catch (Throwable throwable) {
            desc = "异常：" + getExceptionMsg(throwable);
            throw throwable;
        } finally {
            String msg = null;
            try {
                msg = JSON.toJSONString(point.getArgs());
            } catch (Exception e) {
                msg = "null";
            }
            String retStr = null;
            if (ret == null) {
                retStr = "null";
            } else if (ret instanceof PageInfo) {
                PageInfo pageinfo = (PageInfo) ret;
                retStr = String.format("PageInfo分页查询结果总数量：%s", pageinfo.getTotal());
            } else if (ret instanceof List) {
                List list = (List) ret;
                retStr = String.format("List查询结果总数量：%s", list.size());
            } else {
                retStr = JSON.toJSONString(ret);
            }
           logger.info("****   性能监控，方法[{}.{}]执行耗时: {} ms, 结果：{}, 参数：{}, 响应：{}", 
                   point.getTarget().getClass().getSimpleName(), 
                   point.getSignature().getName(), 
                   (System.currentTimeMillis()-start), 
                   desc,
                   point.getArgs() == null ? "null" : msg,
                   retStr.length() < 3000? retStr:retStr.substring(0, 3000)); 
        }
        return ret;
    }
    
    private String getExceptionMsg(Throwable e) {
        if (e == null) {
            return null;
        }
        if (e.getMessage() != null && !"null".equals(e.getMessage()) && !"".equals(e.getMessage())) {
            return e.getMessage();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage() == null ? "" : e.getMessage());
        StackTraceElement[] steArr = e.getStackTrace();
        int rowIndx = 0;
        for (StackTraceElement ste : steArr) {
            String exClassName = ste.getClassName();
            if (rowIndx++ <= 5 || exClassName.startsWith("com.yangk")) {
                sb.append("\n").append(ste.getClassName()).append("(").append(ste.getMethodName()).append(".")
                    .append(ste.getLineNumber()).append(")");
            }
        }
        return sb.toString();
    }
}
