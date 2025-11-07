package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额数据统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {

        //得到时间字符串  2022-10-01,2022-10-02,2022-10-03
        List<LocalDate> localDateList = new ArrayList<>();
        localDateList.add(begin);

        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }
        //String localDateSring = JSON.toJSONString(localDateList);

        //得到营业额字符串
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            //根据订单状态和时间查询
            //select * from orders where status = "已完成“ and order_time >localDate.min and order_time <localDate.max
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            //封装为一个map
            Map map = new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("begin",beginTime);
            map.put("end",endTime);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(StringUtils.join(localDateList,","));
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList,","));

        return turnoverReportVO;
    }

    /**
     * 根据时间区间统计用户数量
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>(); //新增用户数
        List<Integer> totalUserList = new ArrayList<>(); //总用户数
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
            //select count(id) where order_time > begin and order_time <end
            Map map = new HashMap();
            map.put("end",endTime);
            Integer totalUser = orderMapper.countByMap(map);
            totalUserList.add(totalUser);

            map.put("begin",beginTime);
            Integer newUser = orderMapper.countByMap(map);
            newUserList.add(newUser);
        }

        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(StringUtils.join(dateList,","));
        userReportVO.setTotalUserList(StringUtils.join(totalUserList,","));
        userReportVO.setNewUserList(StringUtils.join(newUserList,","));

        return userReportVO;
    }
}
