package org.example.controller;

import com.alibaba.fastjson2.JSONObject;
import org.example.core.annotation.Controller;
import org.example.core.annotation.PostMapping;
import org.example.core.annotation.RequestBody;
import org.example.core.annotation.ResponseBody;
import org.example.entity.User;

@Controller
public class UserController {

    @ResponseBody
    @PostMapping(path = "/user")
    public String createUser(@RequestBody User user) {
        System.out.println("接收到用户数据: " + user);

        JSONObject response = new JSONObject();
        response.put("message", "用户创建成功");
        response.put("user", user);

        return "12333333你好";
    }
}
