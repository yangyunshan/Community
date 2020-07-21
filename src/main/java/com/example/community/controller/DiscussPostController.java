package com.example.community.controller;

import com.example.community.dao.CommentMapper;
import com.example.community.entity.Comment;
import com.example.community.entity.DiscussPost;
import com.example.community.entity.Page;
import com.example.community.entity.User;
import com.example.community.service.CommentService;
import com.example.community.service.DiscussPostService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import com.example.community.util.CommunityUtil;
import com.example.community.util.HostHoler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHoler hostHoler;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHoler.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错的情况，之后单独处理
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);

        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //评论：帖子的评论
        //回复：给评论的评论

        //评论列表
        List<Comment> comments = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(),page.getLimit());
        //评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        if (comments != null) {
            for (Comment comment : comments) {
               Map<String, Object> commentVo = new HashMap<>();
               //评论
               commentVo.put("comment", comment);
               commentVo.put("user", userService.findUserById(comment.getUserId()));
               //回复列表
               List<Comment> replys = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
               //回复VO列表
               List<Map<String, Object>> replyVoList = new ArrayList<>();
               if (replys != null) {
                   for (Comment reply : replys) {
                       Map<String, Object> replyVo = new HashMap<>();
                       replyVo.put("reply", reply);
                       replyVo.put("user", userService.findUserById(reply.getUserId()));
                       //回复目标
                       User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                       replyVo.put("target", target);
                       replyVoList.add(replyVo);
                   }
               }
               commentVo.put("reply", replyVoList);

               //回复数量
               int replyCount = commentService.findCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
               commentVo.put("replyCount", replyCount);
               commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }


}
