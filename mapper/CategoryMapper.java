package com.example.reggie_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.service.CategoryService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
