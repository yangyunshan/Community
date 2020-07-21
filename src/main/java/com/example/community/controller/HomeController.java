package com.example.community.controller;

import com.example.community.entity.DiscussPost;
import com.example.community.entity.Page;
import com.example.community.entity.User;
import com.example.community.service.DiscussPostService;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService disscussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {//此处page会自动包装前台传回的参数，且会自动装配到Model
        page.setRows(disscussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = disscussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String, Object>> discussposts = new ArrayList<>();

        if (list!=null) {
            for (DiscussPost discussPost : list) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("post",discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                temp.put("user",user);
                discussposts.add(temp);
            }
        }
        model.addAttribute("discussPosts",discussposts);

        return "index";
    }

    @RequestMapping(path = "error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }
}
