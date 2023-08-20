package com.hqy.cloud.poi.test;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @date 2023-08-17 20:36
 */

public class Test {

    public static void main(String[] args) {
        ImportParams params = new ImportParams();
        try {
            FileInputStream fileInputStream = new FileInputStream("C:/Users/Administrator/Desktop/test/月人数.xlsx");
            List<DepartmentDTO> departmentList = ExcelImportUtil.importExcel(fileInputStream, DepartmentDTO.class, params);
            Map<String, DepartmentDTO> departmentMap = departmentList.stream().collect(Collectors.toMap(DepartmentDTO::getName, v -> v));

            FileInputStream fileInputStream2 = new FileInputStream("C:/Users/Administrator/Desktop/test/原始记录.xlsx");
            List<ClockInDTO> clockInList = ExcelImportUtil.importExcel(fileInputStream2, ClockInDTO.class, params);

            Map<String, List<ClockInDTO>> map = clockInList.stream().collect(Collectors.groupingBy(ClockInDTO::getName));
            Set<Map.Entry<String, List<ClockInDTO>>> entries = map.entrySet();
            List<Map.Entry<String, List<ClockInDTO>>> collect = entries.stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());
            List<ClockInVO> vos = new ArrayList<>();
            for (Map.Entry<String, List<ClockInDTO>> entry : collect) {
                List<ClockInDTO> value = entry.getValue();
                Map<String, List<ClockInDTO>> listMap = value.stream().peek(clockInDTO -> {
                    Date clockIn = clockInDTO.getClockIn();
                    String date = DateUtil.format(clockIn, DatePattern.NORM_DATE_PATTERN);
                    clockInDTO.setDate(date);
                    String time = DateUtil.format(clockIn, "HH:mm");
                    clockInDTO.setTime(time);
                }).collect(Collectors.groupingBy(ClockInDTO::getDate));
                List<ClockInVO> same = new ArrayList<>();
                for (List<ClockInDTO> clock : listMap.values()) {
                    String name = clock.get(0).getName();
                    DepartmentDTO departmentDTO = departmentMap.get(name);
                    String department = departmentDTO == null ? "" : departmentDTO.getDepartment();
                    String relationship = departmentDTO == null ? "" : departmentDTO.getRelationship();

                    clock.sort((c1, c2) -> (int) (DateUtil.parse(c1.getTime(), "HH:mm").getTime() - DateUtil.parse(c2.getTime(), "HH:mm").getTime()));
                    String date = clock.get(0).getDate();
                    String min = clock.get(0).getTime();
                    String max = clock.get(clock.size() - 1).getTime();
                    ClockInVO build = ClockInVO.builder()
                            .name(name)
                            .department(department)
                            .relationship(relationship)
                            .date(date)
                            .minTime(min)
                            .maxTime(max).build();
                    same.add(build);
                }
                same = same.stream().sorted(Comparator.comparingLong(v -> DateUtil.parse(v.getDate(), DatePattern.NORM_DATE_PATTERN).getTime())).collect(Collectors.toList());
                vos.addAll(same);
            }

            Workbook sheets = ExcelExportUtil.exportExcel(new ExportParams("打卡表", "clockIn"), ClockInVO.class, vos);
            FileOutputStream fileOutputStream = new FileOutputStream("C:/Users/Administrator/Desktop/test/打卡信息表.xlsx");
            sheets.write(fileOutputStream);
            fileOutputStream.close();
            sheets.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
