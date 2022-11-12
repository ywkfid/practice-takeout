package com.ywk.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.Employee;
import com.ywk.takeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /**
     * 员工登陆
     * @param request 获取session存储 到时候取信息方便
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("登陆员工，员工信息:{}", employee.toString());
        return employeeService.login(request, employee);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除session中的数据
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息:{}", employee.toString());
        return employeeService.create(request, employee);
    }

    /**
     * 分页查询员工信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("员工信息:page = {}, pageSize = {}, name = {}", page, pageSize, name);
        return employeeService.page(page, pageSize, name);
    }

    /**
     * 根据id更新员工信息
     */
    @PutMapping
    public R<String> updateData(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("更新员工信息:{}", employee.toString());
        return employeeService.update(request, employee);
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息:{}", id);
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("未查询到员工对应信息");
    }

}
