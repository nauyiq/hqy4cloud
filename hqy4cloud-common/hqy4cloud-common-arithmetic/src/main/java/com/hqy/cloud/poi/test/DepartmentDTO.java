package com.hqy.cloud.poi.test;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @date 2023-08-17 20:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelTarget("Department")
public class DepartmentDTO {

    @Excel(name = "姓名")
    private String name;
    @Excel(name = "劳动关系")
    private String relationship;
    @Excel(name = "成本部门")
    private String department;

}
