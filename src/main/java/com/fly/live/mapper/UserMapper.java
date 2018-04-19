package com.fly.live.mapper;

import com.fly.live.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkPhoneNum(String phonenum);

    int checkEmail(String email);

    User selectLogin(@Param("phone") String phone, @Param("password") String password);
}