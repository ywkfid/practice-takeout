package com.ywk.takeout.filter;

import com.alibaba.fastjson.JSON;
import com.ywk.takeout.common.BaseContext;
import com.ywk.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登陆拦截器，防止不登陆也能访问页面，/*表示任何路径都拦截
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器spring提供 支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获得本次请求的url
        String requestURI = request.getRequestURI();
        log.info("拦截到请求,{}", requestURI);
        //判断是否需要处理
        String[] urls = new String[] {
                "/employee/login", //直接放行
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(urls, requestURI);
        //不需要处理就放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //判断登陆状态，如果已经登陆，就直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登陆，用户id为: {}", request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}", id);

            filterChain.doFilter(request, response);
            return;
        }

        //判断移动端登陆状态，如果已经登陆，就直接放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登陆，用户id为: {}", request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登陆");
        //如果为登陆就返回未登陆结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
