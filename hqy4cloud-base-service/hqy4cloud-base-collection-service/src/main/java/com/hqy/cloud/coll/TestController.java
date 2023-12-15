package com.hqy.cloud.coll;

import com.hqy.cloud.coll.entity.SqlRecord;
import com.hqy.cloud.coll.mapper.SqlRecordTkMapper;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/13 15:47
 */
@RestController
@RequiredArgsConstructor
public class TestController {
    private final SqlRecordTkMapper mapper;

    @GetMapping("/test")
    public R<Boolean> test() {
        SqlRecord sqlRecord = new SqlRecord();
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            sqlRecord.setApplication("test");
            sqlRecord.setType(1);
        }
        mapper.insert(sqlRecord);
        return R.ok();
    }

}
