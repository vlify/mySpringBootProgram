package com.sky.mapper;

import com.sky.entity.Employee;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

  /**
   * 根据用户名查询员工
   * 
   * @param username
   * @return
   */
  @Select("select * from employee where username = #{username}")
  Employee getByUsername(String username);

  /**
   * insert employee
   * @ param employee
   */
  @Insert("insert into employee (name, usernamem, password, sex, id_number, phone, status, create_user,create_time,update_user,updat_time)"
      +
      "values "
      + "(#{name}, #{username}, #{password}, #{sex}, #{idNumber}, #{phone}, #{status}) , #{ createUser}, #{createTime}, #{updateUser}, #{updateTime})")
  void insert(Employee employee);

}
