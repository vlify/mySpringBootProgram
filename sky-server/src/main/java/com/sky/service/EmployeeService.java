package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

  /**
   * 员工登录
   * 
   * @param employeeLoginDTO
   * @return
   */
  Employee login(EmployeeLoginDTO employeeLoginDTO);

  /**
   * add or update employee
   * 
   * @param employeeDTO
   */
  void save(EmployeeDTO employeeDTO);

  /**
   * page query
   * 
   * @param employeePageQueryDTO
   * @return
   */
  PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

  /**
   * 启用禁用员工账号
   * 
   * @param status
   * @return
   */
  void startOrStop(Integer status, Long id);

}
