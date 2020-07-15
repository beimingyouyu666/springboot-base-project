package com.yangk.baseproject.common.config;

import com.yangk.baseproject.common.filter.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @Description 注册过滤器
 * @Author yangkun
 * @Date 2020/6/29
 * @Version 1.0
 * @blame yangkun
 */
@Configuration
public class FilterRegistrationConfig {

    @Bean
    public FilterRegistrationBean<Filter>  filterRegistrationBean() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<Filter>();
        bean.setFilter(new LogFilter());
        bean.setEnabled(true);
        bean.addUrlPatterns("/*");
        return bean;
    }
}
