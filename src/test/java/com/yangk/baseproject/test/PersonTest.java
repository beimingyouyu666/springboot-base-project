package com.yangk.baseproject.test;

import com.yangk.baseproject.WmsAuApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Description 使用map将person接口所有的实现类都注入进来
 * @Author yangkun
 * @Date 2020/7/3
 * @Version 1.0
 * @blame yangkun
 */
public class PersonTest extends WmsAuApplicationTests {

    @Autowired
    private Map<String,Person> map;

    @Test
    public void say() {
        String s1 = "personImpl1";
        String s2 = "personImpl2";
        Person person = map.get(s1);
        Person person1 = map.get(s2);
        person.say();
        System.out.println("===================");
        person1.say();
    }
}