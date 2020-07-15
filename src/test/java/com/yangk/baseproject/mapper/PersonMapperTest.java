package com.yangk.baseproject.mapper;

import com.yangk.baseproject.WmsAuApplicationTests;
import com.yangk.baseproject.domain.dos.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonMapperTest extends WmsAuApplicationTests {

    @Autowired
    private PersonMapper personMapper;

    @Test
    public void selectByPrimaryKey() {
        Person person = personMapper.selectByPrimaryKey(1L);
        System.out.println(person);
    }
}