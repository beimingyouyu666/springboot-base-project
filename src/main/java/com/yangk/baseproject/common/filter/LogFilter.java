package com.yangk.baseproject.common.filter;

import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @Description 针对每一个请求线程设置一个 traceId
 *  logback配置文件中 PatternLayout  加上 [%X{traceId}]
 * @Author yangkun
 * @Date 2020/4/9
 * @Version 1.0
 * @blame yangkun
 */
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {

        MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-",""));
        try {
            chain.doFilter(req, resp);
        } catch (Exception e) {
            throw e;
        } finally {
            MDC.remove("traceId");
        }
    }

    @Override
    public void destroy() {

    }

}
