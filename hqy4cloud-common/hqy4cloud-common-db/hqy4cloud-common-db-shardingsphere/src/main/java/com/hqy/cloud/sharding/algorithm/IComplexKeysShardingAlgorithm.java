package com.hqy.cloud.sharding.algorithm;

import com.hqy.cloud.sharding.id.DistributedID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 混合键分片策略
 * @author qiyuan.hong
 * @date 2024/7/22
 */
@Slf4j
public class IComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm<String>, HintShardingAlgorithm<String> {
    private Properties props;

    private static final String PROP_MAIN_COLUM = "mainColum";

    private static final String PROP_TABLE_COUNT = "tableCount";


    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> complexKeysShardingValue) {
        Collection<String> result = new HashSet<>();

        String mainColum = props.getProperty(PROP_MAIN_COLUM);
        // 获取分片键的值
        Collection<String> mainColumCollection = complexKeysShardingValue.getColumnNameAndShardingValuesMap().get(mainColum);
        if (CollectionUtils.isNotEmpty(mainColumCollection)) {
            for (String colum : mainColumCollection) {
                String shardingTarget = calculateShardingTarget(colum);
                result.add(shardingTarget);
            }
            return getMatchedTables(result, availableTargetNames);
        }

        complexKeysShardingValue.getColumnNameAndShardingValuesMap().remove(mainColum);
        Collection<String> otherColumCollection = complexKeysShardingValue.getColumnNameAndShardingValuesMap().keySet();
        if (CollectionUtils.isNotEmpty(otherColumCollection)) {
            for (String colum : otherColumCollection) {
                Collection<String> otherColumValues = complexKeysShardingValue.getColumnNameAndShardingValuesMap().get(colum);
                for (String value : otherColumValues) {
                    String shardingTarget = extractShardingTarget(value);
                    result.add(shardingTarget);
                }
            }
            return getMatchedTables(result, availableTargetNames);
        }
        return null;
    }



    @Override
    public Collection<String> doSharding(Collection<String> collection, HintShardingValue<String> hintShardingValue) {
        String logicTableName = hintShardingValue.getLogicTableName();
        Collection<String> shardingTargets = hintShardingValue.getValues();

        Collection<String> matchedTables = new HashSet<>();
        for (String shardingTarget : shardingTargets) {
            matchedTables.add(logicTableName + "_" + shardingTarget);
        }

        log.info("matchedTables : {}", matchedTables);
        return CollectionUtils.intersection(collection, matchedTables);
    }


    private String calculateShardingTarget(String externalId) {
        String tableCount = props.getProperty(PROP_TABLE_COUNT);
        return DistributedID.getShardingTable(externalId, Integer.parseInt(tableCount));
    }

    private Collection<String> getMatchedTables(Collection<String> results, Collection<String> availableTargetNames) {
        Collection<String> matchedTables = new HashSet<>();
        for (String result : results) {
            matchedTables.addAll(availableTargetNames.parallelStream().filter(each -> each.endsWith(result)).collect(Collectors.toSet()));
        }
        return matchedTables;
    }

    private String extractShardingTarget(String value) {
        return DistributedID.getShardingTable(value);
    }


    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void init(Properties properties) {
        this.props = properties;
    }


}
