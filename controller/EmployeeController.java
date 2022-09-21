package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Employee;
import com.example.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(
            HttpServletRequest request,
            @RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        String username = employee.getUsername();
        QueryWrapper<Employee> employeeQueryWrapper = new QueryWrapper<>();
        employeeQueryWrapper.eq("username", username);
        Employee emp = employeeService.getOne(employeeQueryWrapper);
        //3、如果没有查询到则返回登录失败结果
        if(null == emp) {
            return R.error("登录失败，查无此账号");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)) {
            return R.error("登录失败，密码错误");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0) {
            return R.error("登录失败，该账号已被禁用");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(
            HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
//        Long id = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);
        employeeService.save(employee);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R page(
            int page,
            int pageSize,
            String name
    ){
        Page<Employee> empPage = new Page<>(page, pageSize);
        QueryWrapper<Employee> employeeWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            employeeWrapper.like("name", name);
        }
        Page<Employee> page1 = employeeService.page(empPage, employeeWrapper);
        return R.success(page1);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
        employeeService.updateById(employee);
        return R.success("修改完畢");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(null != employee) {
            return R.success(employee);
        }
        return R.error("没有查到员工信息");
    }

}
