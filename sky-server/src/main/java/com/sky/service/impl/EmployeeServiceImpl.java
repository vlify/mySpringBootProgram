package com.sky.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

  @Autowired
  private EmployeeMapper employeeMapper;

  /**
   * 员工登录
   *
   * @param employeeLoginDTO
   * @return
   */
  public Employee login(EmployeeLoginDTO employeeLoginDTO) {
    String username = employeeLoginDTO.getUsername();
    String password = employeeLoginDTO.getPassword();

    // 1、根据用户名查询数据库中的数据
    Employee employee = employeeMapper.getByUsername(username);

    // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
    if (employee == null) {
      // 账号不存在
      throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
    }

    // 密码比对
    // 进行md5加密，然后再进行比对
    password = DigestUtils.md5DigestAsHex(password.getBytes());
    if (!password.equals(employee.getPassword())) {
      // 密码错误
      throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
    }

    if (employee.getStatus() == StatusConstant.DISABLE) {
      // 账号被锁定
      throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
    }

    // 3、返回实体对象
    return employee;
  }

  /**
   * add or update employee
   *
   * @param employeeDTO
   */
  public void save(EmployeeDTO employeeDTO) {
    Employee employee = new Employee();

    // copy the param
    BeanUtils.copyProperties(employeeDTO, employee);

    // set status
    employee.setStatus(StatusConstant.ENABLE);

    // set password
    employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

    // set create time and update time
    employee.setCreateTime(LocalDateTime.now());
    employee.setUpdateTime(LocalDateTime.now());

    // set create user and update user
    employee.setCreateUser(BaseContext.getCurrentId());
    employee.setUpdateUser(BaseContext.getCurrentId());

    employeeMapper.insert(employee);
  }

  /**
   * 分页查询
   *
   * @param employeePageQueryDTO
   * @return
   */
  public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {

    // 开始分页查询
    // 使用 PageHelper 进行分页查询,自动计算页码与分页大小
    PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

    Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

    long total = page.getTotal();
    List<Employee> records = page.getResult();

    return new PageResult(total, records);
  }

  /**
   * start or stop employee account
   * 
   * @param status
   * @return
   */
  @Override
  public void startOrStop(Integer status, Long id) {
    Employee employee = Employee.builder().id(id).status(status).updateTime(LocalDateTime.now())
        .updateUser(BaseContext.getCurrentId()).build();
    employeeMapper.update(employee);
  }

}
