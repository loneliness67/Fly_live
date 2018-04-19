package com.fly.live.controller;

import com.fly.live.common.Const;
import com.fly.live.model.Live;
import com.fly.live.model.User;
import com.fly.live.service.IFileService;
import com.fly.live.service.IPlayService;
import com.fly.live.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class PlayController {

    @Autowired
    private IPlayService playService;

    @Autowired
    private IFileService iFileService;


    @RequestMapping("/{id}")
    public String live(@PathVariable("id") Integer id, HttpSession session) {
        Live live = playService.getLive(id);
        if (live == null) {
            return "/nofind";
        }
        session.setAttribute(Const.CURRENT_LIVE, live);

        return "/live";
    }

    @GetMapping("/live")
    public String exitLive(HttpSession session, Model model) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            System.out.println("没登陆过");
            return "redirect:login";
        }

        Live live = playService.getLive(user.getLiveId());
        model.addAttribute(Const.MY_LIVE, live);

        return "/main";
    }

    @PostMapping("/live")
    public String updateLive(String name, String detail, Integer categoryId, String welcome, HttpSession session, Model model) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            System.out.println("没登陆过");
            return "redirect:login";
        }

        Live live = new Live();
        live.setId(user.getLiveId());
        if (!StringUtils.isBlank(name)) {
            live.setName(name);
        }

        if (detail.length() > 18) {
            live.setDetail(detail);
        }

        if (!StringUtils.isBlank(welcome)) {
            live.setWelcome(welcome);
        }

        live.setCategoryId(categoryId);

        playService.updateLive(live);
//        model.addAttribute("success", 1);
        String str = "/" + live.getId();


        return "redirect:" + str;

    }

    @RequestMapping("/random")
    public String randomRtmp(HttpSession session, Model model) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        playService.randomRtmp(user.getLiveId());
        Live live = playService.getLive(user.getLiveId());
        model.addAttribute(Const.MY_LIVE, live);

        return "redirect:live";
    }

    @PostMapping("/liveupload")
    public void upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {

        System.out.println("开始上传海报");
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if ( user == null) {
            System.out.println("没登陆过");
            return ;
        }


        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

//            Map fileMap = Maps.newHashMap();
//            fileMap.put("uri", targetFileName);
//            fileMap.put("url", url);
        url = url.replace("=", "");
        Live live = new Live();
        live.setId(user.getLiveId());
        live.setImages(url);
        playService.updateLive(live);

        System.out.println("海报上传完成");
    }
}
