package com.example.database_cli.mapper;

import com.example.database_cli.model.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
    
    // 根据ID查询管理员
    Admin selectById(@Param("id") String id);
    
    // 查询所有管理员
    List<Admin> selectAll();
    
    // 插入管理员
    int insert(Admin admin);
    
    // 更新管理员信息
    int update(Admin admin);
    
    // 根据ID删除管理员
    int deleteById(@Param("id") String id);
    
    // 根据用户名和密码查询管理员
    Admin selectByUsernameAndPassword(@Param("id") String id, @Param("password") String password);
} 