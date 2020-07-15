package com.yangk.baseproject.mapper;

import com.yangk.baseproject.WmsAuApplicationTests;
import com.yangk.baseproject.domain.dos.UserDO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMapperTest extends WmsAuApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void getUserById() {
        UserDO user = userMapper.getUserById("11");
        System.out.println(user);
    }
}