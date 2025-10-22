package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminController")
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "管理端套餐接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    // 新增套餐
    @PostMapping
    @ApiOperation("新增套餐")
    public Result add(@RequestBody SetmealDTO setmealDTO){
        setmealService.addWithDish(setmealDTO);
        return Result.success();
    }

    // 套餐分页查询
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        //先根据条件进行动态查询 pageHelper是进行分页的
        //查询套餐表（德奥套餐基本信息）和分类表（分类名称）
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(pageResult);
    }


    // 删除套餐


    // 修改套餐


    // 起售停售套餐
}
