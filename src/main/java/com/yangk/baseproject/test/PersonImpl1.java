package com.yangk.baseproject.test;

import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author yangkun
 * @Date 2020/7/3
 * @Version 1.0
 * @blame yangkun
 */
@Component
public class PersonImpl1 implements Person {
    @Override
    public void say() {
        System.out.println("say PersonImpl1");
    }
}
