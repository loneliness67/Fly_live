package com.fly.live.service;

import com.fly.live.common.ServerResponse;
import com.fly.live.model.User;

public interface IUserService {
    String register(User user);
    ServerResponse<User> login(String phone, String password);
    User edit(User user);
    void saveAvatar(Integer id,String url);
}
