package com.yangk.baseproject.mapper;

import com.yangk.baseproject.SpringbootBaseProjectApplicationTests;
import com.yangk.baseproject.domain.dos.UserDO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMapperTest extends SpringbootBaseProjectApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void getUserById() {
        UserDO user = userMapper.getUserById("11");
        System.out.println(user);
    }
}