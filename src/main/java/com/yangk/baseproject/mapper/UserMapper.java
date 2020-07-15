package com.yangk.baseproject.mapper;

import com.yangk.baseproject.domain.dos.UserDO;

/**
 * @Description
 * @Author yangkun
 * @Date 2020/7/6
 * @Version 1.0
 * @blame yangkun
 */
public interface UserMapper {

    UserDO getUserById(String userId);
}
