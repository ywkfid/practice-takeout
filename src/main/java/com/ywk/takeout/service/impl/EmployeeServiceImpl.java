package com.ywk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.Employee;
import com.ywk.takeout.mapper.EmployeeMapper;
import com.ywk.takeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

    /**
     * 1.密码进行md5加密
     * 2.查询username是否存在
     * 3.如果用户名不存在直接返回失败
     * 4.比对密码不成功就返回失败
     * 5.看账户是否被禁用
     * 6.登陆成功，将员工id存入session并返回成功
     */
    @Override
    public R<Employee> login(HttpServletRequest request, Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = this.getOne(queryWrapper);

        if (emp == null) {
            return R.error("登陆失败");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 新增员工，密码进行md5加密
     * @param request
     * @param employee
     * @return
     */
    @Override
    public R<String> create(HttpServletRequest request, Employee employee) {
        //设置初始密码，并进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //获取当前登陆用户Id
        Long empId = (Long)request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        super.save(employee);

        return R.success("新增代码成功");
    }

    /**
     * 员工分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器
        Page pageInfo = new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //查询name是否为null，否的话就查询
        queryWrapper.like(StringUtils.hasText(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //分页搜索
        this.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    @Override
    public R<String> update(HttpServletRequest request, Employee employee) {
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}", id);
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        //直接更新不会成功 雪花算法会有精度损失 js对long数据只能保证前16位准确
        //所以把long改成string
        this.updateById(employee);
        return R.success("员工信息修改成功");
    }
}
