package com.hqy.cloud.poi.test;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @date 2023-08-17 21:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelTarget("clockInVO")
public class ClockInVO {

    @Excel(name = "姓名")
    private String name;
    @Excel(name = "劳动关系")
    private String relationship;
    @Excel(name = "成本部门")
    private String department;
    @Excel(name = "日期")
    private String date;
    @Excel(name = "最小值项:打卡时间")
    private String minTime;
    @Excel(name = "最大值项:打卡时间")
    private String maxTime;


}
