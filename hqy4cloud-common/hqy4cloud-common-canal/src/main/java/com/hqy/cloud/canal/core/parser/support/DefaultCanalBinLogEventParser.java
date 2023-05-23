package com.hqy.cloud.canal.core.parser.support;

import com.hqy.cloud.canal.common.BinLogEventType;
import com.hqy.cloud.canal.common.OperationType;
import com.hqy.cloud.canal.core.parser.BaseCommonEntryFunction;
import com.hqy.cloud.canal.core.parser.BasePrimaryKeyTupleFunction;
import com.hqy.cloud.canal.core.parser.CanalBinLogEventParser;
import com.hqy.cloud.canal.model.CanalBinLogEvent;
import com.hqy.cloud.canal.model.CanalBinLogResult;
import com.hqy.cloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * DefaultCanalBinLogEventParser.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/18 9:58
 */
@Slf4j
public class DefaultCanalBinLogEventParser implements CanalBinLogEventParser {
    private DefaultCanalBinLogEventParser() {
    }

    public static CanalBinLogEventParser of() {
        return new DefaultCanalBinLogEventParser();
    }

    @Override
    public <T> List<CanalBinLogResult<T>> parse(CanalBinLogEvent event, Class<T> aClass, BasePrimaryKeyTupleFunction pkTupleFunction, BaseCommonEntryFunction<T> entryFunction) {
        BinLogEventType eventType = BinLogEventType.fromType(event.getType());
        //不监听DLL事件
        if (Objects.equals(BinLogEventType.CREATE, eventType) || Objects.equals(BinLogEventType.ALTER, eventType)) {
            if (log.isDebugEnabled()) {
                log.debug("Receiver not support binlog event type = {}, ignore this binlog event = {}.",eventType, JsonUtil.toJson(event));
            }
            return Collections.emptyList();
        }

        if (BinLogEventType.UNKNOWN != eventType && BinLogEventType.QUERY != eventType) {
            if (Boolean.TRUE.equals(event.getIsDdl())) {
                CanalBinLogResult<T> entry = new CanalBinLogResult<>();
                entry.setOperationType(OperationType.DDL);
                entry.setBinLogEventType(eventType);
                entry.setDatabaseName(event.getDatabase());
                entry.setTableName(event.getTable());
                entry.setSql(event.getSql());
                return Collections.singletonList(entry);
            } else {
                List<String> pkNames = event.getPkNames();
                //DML类型binlog事件主键列数量不为1
                if (CollectionUtils.isEmpty(pkNames) || pkNames.size() != 1) {
                    throw new IllegalArgumentException("The number of primary key columns in the DML table is not 1.");
                }
                List<CanalBinLogResult<T>> entryList = new LinkedList<>();
                String pkName = event.getPkNames().get(0);
                List<Map<String, String>> data = event.getData();
                List<Map<String, String>> old = event.getOld();
                int dataSize = null != data ? data.size() : 0;
                int oldSize = null != old ? old.size() : 0;
                if (dataSize > 0) {
                    for(int index = 0; index < dataSize; ++index) {
                        CanalBinLogResult<T> entry = new CanalBinLogResult<>();
                        entryList.add(entry);
                        entry.setSql(event.getSql());
                        entry.setOperationType(OperationType.DML);
                        entry.setBinLogEventType(eventType);
                        entry.setTableName(event.getTable());
                        entry.setDatabaseName(event.getDatabase());
                        Map<String, String> item = data.get(index);
                        entry.setAfterData(entryFunction.apply(item));
                        Map<String, String> oldItem = null;
                        if (oldSize > 0 && index <= oldSize) {
                            oldItem = old.get(index);
                            entry.setBeforeData(entryFunction.apply(oldItem));
                        }
                        entry.setPrimaryKey(pkTupleFunction.apply(oldItem, item, pkName));
                    }
                }
                return entryList;
            }
        } else {
            //不支持的DML事件
            if (log.isDebugEnabled()) {
                log.debug("Receiver not support binlog event type = {}, ignore this binlog event = {}.",eventType, JsonUtil.toJson(event));
            }
            return Collections.emptyList();
        }
    }
}
