package com.fly.live.service.Impl;

import com.fly.live.common.Const;
import com.fly.live.common.ServerResponse;
import com.fly.live.mapper.LiveMapper;
import com.fly.live.mapper.UserMapper;
import com.fly.live.model.Live;
import com.fly.live.model.User;
import com.fly.live.service.IUserService;
import com.fly.live.util.MD5Util;
import com.fly.live.util.PropertiesUtil;
import com.fly.live.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LiveMapper liveMapper;

    @Override
    public String register(User user) {
        user.setRole(0);
        user.setStatuc(0);
        String str = checkValid(user.getPhone(), Const.PHONENUM);
        if (!str.equals("校验成功")) {
            return str;
        }
        str = checkValid(user.getUsername(), Const.USERNAME);
        if (!str.equals("校验成功")) {
            return str;
        }
        String md5Password = MD5Util.MD5EncodeUtf8(user.getPassword());
        user.setPassword(md5Password);

        user.setLiveId(createLive());

        user.setImage(PropertiesUtil.getProperty("default.img", ""));
        userMapper.insert(user);
        return "注册成功";
    }

    public String checkValid(String str, String type) {
        int resultCount = 0;
        if (Const.USERNAME.equals(type)) {
            resultCount = userMapper.checkUsername(str);
            if (resultCount > 0) {
                return "昵称已存在";
            }
        }

        if (Const.EMAIL.equals(type)) {
            resultCount = userMapper.checkEmail(str);
            if (resultCount > 0) {
                return "邮箱已存在";
            }
        }

        if (Const.PHONENUM.equals(type)) {
            resultCount = userMapper.checkPhoneNum(str);
            if (resultCount > 0) {
                return "手机号已存在";
            }
        }

        return "校验成功";
    }

    public ServerResponse<User> login(String phone, String password) {

        int resultCount = userMapper.checkPhoneNum(phone);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("手机号不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(phone, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        if (user.getStatuc() == 1) {
            return ServerResponse.createByErrorMessage("用户被限制登陆");
        }
        return ServerResponse.createBySuccess(user);
    }


    //创建直播间

    public Integer createLive() {
        Integer LiveId = RandomUtil.getFiveRandomUtil();

        while (checkLveId(LiveId)) {
            LiveId = RandomUtil.getFiveRandomUtil();
        }
        Live live = new Live();
        live.setId(LiveId);
        live.setName("无标题");
        live.setStatus(0);
        live.setCategoryId(0);
        live.setCode(UUID.randomUUID().toString().replace("-",""));
        liveMapper.insert(live);
        return LiveId;
    }


    //为真的时候存在
    public boolean checkLveId(int id) {
        int resultCount = liveMapper.checkById(id);
        if (resultCount > 0) {
        return true;
        }
            return false;
    }

    public User edit(User user) {
        if (!StringUtils.isBlank(user.getPassword())) {
            String MD5Password = MD5Util.MD5EncodeUtf8(user.getPassword());
            user.setPassword(MD5Password);
        }else {
            user.setPassword(null);
        }
        if (!StringUtils.isBlank(user.getEmail())) {
            String tmp = checkValid(user.getEmail(), Const.EMAIL);
            if (!tmp.equals("校验成功")) {
                user.setEmail(null);
            }
        }
        if (!StringUtils.isBlank(user.getPhone())) {
            String tmp = checkValid(user.getPhone(), Const.PHONENUM);
            if (!tmp.equals("校验成功")) {
                user.setPhone(null);
            }
        }
        userMapper.updateByPrimaryKeySelective(user);
        return userMapper.selectByPrimaryKey(user.getId());
    }

    public void saveAvatar(Integer id,String url) {
        User user = new User();
        user.setId(id);
        user.setImage(url);

        userMapper.updateByPrimaryKeySelective(user);
    }

}
