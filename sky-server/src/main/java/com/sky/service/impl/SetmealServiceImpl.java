package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 根据条件动态查询套餐
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据套餐id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    /**
     * 添加套餐
     * @param setmealDTO
     */
    public void addWithDish(SetmealDTO setmealDTO) {
        //setmealDTO包含套餐基本信息和菜品信息
        //首先得到套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //将套餐数据插入到套餐表中
        setmealMapper.insert(setmeal);

        //菜品信息是集合
        List<SetmealDish> setmealDishLists = setmealDTO.getSetmealDishes();
        for(SetmealDish sd:setmealDishLists){
            sd.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.insertBatch(setmealDishLists);

    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //limit
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        //动态查询套餐信息

        Page<SetmealVO> list = setmealMapper.pageQuery(setmealPageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(list.getTotal());
        pageResult.setRecords(list);
        return pageResult;
    }

    /**
     * 根据套餐id删除套餐
     * @param ids
     */
    public void delete(List<Integer> ids) {
        for (Integer id :ids){
            //根据套餐id得到套餐
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ON_SALE);
            }
            //在停售状态
            //删除套餐
            setmealMapper.delete(id);
            //删除套餐相关联的菜品
            setmealDishMapper.deleteWithDish(id);
        }
    }
}
