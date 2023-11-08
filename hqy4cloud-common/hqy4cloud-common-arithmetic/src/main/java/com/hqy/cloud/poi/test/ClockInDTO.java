package com.hqy.cloud.poi.test;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @date 2023-08-17 21:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelTarget("clockIn")
public class ClockInDTO {

    @Excel(name = "姓名")
    private String name;
    @Excel(name = "打卡时间", format  = "yyyy-MM-dd HH:mm")
    private Date clockIn;

    private String date;
    private String time;





}
