package com.hqy.collector.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.collector.dao.ThrottledBlockDao;
import com.hqy.collector.entity.ThrottledBlock;
import com.hqy.collector.service.ThrottledBlockService;
import com.hqy.rpc.thrift.struct.PageStruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.hqy.base.common.base.lang.StringConstants.Symbol.PERCENT;

/**
 * @author qiyuan.hong
 * @date 2022-03-01 21:18
 */
@Service
@RequiredArgsConstructor
public class ThrottledBlockServiceImpl extends BaseTkServiceImpl<ThrottledBlock, Long> implements ThrottledBlockService {
    private final ThrottledBlockDao throttledBlockDao;

    @Override
    public BaseDao<ThrottledBlock, Long> getTkDao() {
        return this.throttledBlockDao;
    }


    @Override
    public PageInfo<ThrottledBlock> queryPage(Integer type, String throttleBy, String ip, String url, PageStruct struct) {
        Example example = new Example(ThrottledBlock.class);
        Example.Criteria criteria = example.createCriteria();
        if (Objects.nonNull(type)) {
            criteria.andEqualTo("type", type);
        }
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
