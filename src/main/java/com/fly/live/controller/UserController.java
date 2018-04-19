package com.fly.live.controller;

import com.fly.live.common.Const;
import com.fly.live.common.ServerResponse;
import com.fly.live.model.User;
import com.fly.live.service.IFileService;
import com.fly.live.service.IPlayService;
import com.fly.live.service.IUserService;
import com.fly.live.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IPlayService playService;

    @Autowired
    private IFileService fileService;

    @RequestMapping("/index")
    public String hello() {
        return "/index";
    }

    //注册
    @GetMapping("/register")
    public String getRegister(HttpSession session) {
        if (session.getAttribute(Const.CURRENT_USER) != null) {
            System.out.println("已经登陆过");
            return "redirect:index";
        }
        return "/register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {
        System.out.println("获得到的用户是" + user.toString());
        String str = userService.register(user);
        if (!str.equals("注册成功")) {
            model.addAttribute("resString", str);
            System.out.println("----------------" + str);
            return "/register";
        }
        return "/login";
    }

    //    登陆
    @GetMapping("/login")
    public String getLogin(HttpSession session) {
        if (session.getAttribute(Const.CURRENT_USER) != null) {
            System.out.println("已经登陆过");
            return "redirect:index";
        }
        return "/login";
    }

    @PostMapping("/login")
    public String login(String phone, String password, Model model, HttpSession session) {
        if (session.getAttribute(Const.CURRENT_USER) != null) {
            System.out.println("已经登陆过");
            return "redirect:index";
        }
        ServerResponse<User> serverResponse = userService.login(phone, password);
        if (serverResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());

            return "redirect:index";
        }
        model.addAttribute("resString", serverResponse.getMsg());
        System.out.println(phone + "------" + password);
        return "/login";
    }

    @RequestMapping("/logout")
    public String logont(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return "redirect:index";
    }

    @GetMapping("/edit")
    public String getEdit(HttpSession session) {
        if (session.getAttribute(Const.CURRENT_USER) == null) {
            System.out.println("已经登陆过");
            return "redirect:index";
        }
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return "redirect:index";
        }
        return "/account";
    }

    @PostMapping("/edit")
    public String Edit(Integer sex,String email,String phoneNum,String password,HttpSession session,Model model) {
        if (session.getAttribute(Const.CURRENT_USER) == null) {
            System.out.println("已经登陆过");
            return "redirect:index";
        }
        Integer id=((User)session.getAttribute(Const.CURRENT_USER)).getId();
        User user = new User();
        user.setId(id);
        user.setSex(sex);
        System.out.println("-----------------"+sex);
        user.setEmail(email);
        user.setPhone(phoneNum);
        user.setPassword(password);

        session.setAttribute(Const.CURRENT_USER,userService.edit(user));
        model.addAttribute("success", 1);
        return "/account";
    }

    @GetMapping("/modifyavatar")
    public String modifyavatar(HttpSession session) {
        if (session.getAttribute(Const.CURRENT_USER) == null) {
            System.out.println("已经登陆过");
            return "redirect:index";
        }
        return "/modifyavatar";
    }

    @RequestMapping("/upload")
    public String upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if ( user == null) {
            System.out.println("没登陆过");
            return "redirect:index";
        }


            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = fileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

//            Map fileMap = Maps.newHashMap();
//            fileMap.put("uri", targetFileName);
//            fileMap.put("url", url);
            url = url.replace("=", "");
            userService.saveAvatar(user.getId(),url);
            user.setImage(url);
            session.setAttribute(Const.CURRENT_USER,user);
        return "/account";

    }
}