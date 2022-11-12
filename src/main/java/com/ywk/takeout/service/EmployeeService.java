package com.ywk.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywk.takeout.common.R;
import com.ywk.takeout.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {

    R<Employee> login(HttpServletRequest request, Employee employee);

    R<String> create(HttpServletRequest request, Employee employee);

    R<Page> page(int page, int pageSize, String name);

    R<String> update(HttpServletRequest request, Employee employee);
}
