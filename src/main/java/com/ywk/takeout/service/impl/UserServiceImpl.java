package com.ywk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.User;
import com.ywk.takeout.mapper.UserMapper;
import com.ywk.takeout.service.UserService;
import com.ywk.takeout.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {

    /**
     * 向手机发验证码
     * @param user
     * @param session
     * @return
     */
    @Override
    public R<String> send(User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        //生成随机四位验证码
        if (StringUtils.hasText(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);
            //调用阿里云发送短信
            //不测试 存入session
            session.setAttribute(phone, code);

            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 手机用户登录
     * @param map
     * @param session
     * @return
     */
    @Override
    public R<User> login(Map map, HttpSession session) {

        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        Object codeInsession = session.getAttribute(phone);
        if (codeInsession != null && codeInsession.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = this.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                this.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }

}
