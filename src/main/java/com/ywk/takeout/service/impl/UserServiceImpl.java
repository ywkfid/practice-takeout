package com.ywk.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywk.takeout.entity.User;
import com.ywk.takeout.mapper.UserMapper;
import com.ywk.takeout.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {
}
