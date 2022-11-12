package com.ywk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.User;

import javax.servlet.http.HttpSession;
import java.util.Map;


public interface UserService extends IService<User> {
    R<String> send(User user, HttpSession session);

    R<User> login(Map map, HttpSession session);
}
