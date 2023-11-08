package com.hqy.cloud.coll.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.coll.mapper.ThrottledBlockTkMapper;
import com.hqy.cloud.coll.entity.ThrottledBlock;
import com.hqy.cloud.coll.service.ThrottledBlockService;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.PERCENT;

/**
 * @author qiyuan.hong
 * @date 2022-03-01 21:18
 */
@Service
@RequiredArgsConstructor
public class ThrottledBlockServiceImpl extends BaseTkServiceImpl<ThrottledBlock, Long> implements ThrottledBlockService {
    private final ThrottledBlockTkMapper throttledBlockDao;

    @Override
    public BaseTkMapper<ThrottledBlock, Long> getTkMapper() {
        return this.throttledBlockDao;
    }


    @Override
    public PageInfo<ThrottledBlock> queryPage(String throttleBy, String ip, String url, PageStruct struct) {
        Example example = new Example(ThrottledBlock.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(throttleBy)) {
            criteria.andLike("throttleBy", PERCENT + throttleBy + PERCENT);
        }
        if (StringUtils.isNotBlank(ip)) {
            criteria.andLike("ip", PERCENT + ip + PERCENT);
        }
        if (StringUtils.isNotBlank(url)) {
            criteria.andLike("url", PERCENT + url + PERCENT);
        }
        example.orderBy("id").desc();

        PageHelper.startPage(struct.pageNumber, struct.pageSize);
        List<ThrottledBlock> throttledBlocks = throttledBlockDao.selectByExample(example);
        if (CollectionUtils.isEmpty(throttledBlocks)) {
            return new PageInfo<>();
        }
        return new PageInfo<>(throttledBlocks);
    }
}
