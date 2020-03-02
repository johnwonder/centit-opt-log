package com.centit.framework.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.system.dao.OptLogDao;
import com.centit.framework.system.po.OptLog;
import com.centit.framework.system.service.OptLogManager;
import com.centit.support.database.utils.PageDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service("optLogManager")
public class OptLogManagerImpl implements OptLogManager, OperationLogWriter {

    public static final Logger logger = LoggerFactory.getLogger(OptLogManager.class);


    private OptLogDao optLogDao;

    @Autowired
    @NotNull
    public void setOptLogDao(OptLogDao optLogDao) {
        this.optLogDao = optLogDao;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void saveBatchObjects(List<OptLog> optLogs) {
        if (CollectionUtils.isEmpty(optLogs)) {
            return;
        }
        for (OptLog optLog : optLogs) {
            //if (null == optLog.getLogId()) {
            optLog.setLogId( optLogDao.createNewLogId());
            optLogDao.saveNewObject(optLog);
            //}
        }

    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void delete(Date begin, Date end) {
        Map <String,String>map =new HashMap<String,String>();
        map.put("beginDate", String.valueOf(begin));
        map.put("endDate", String.valueOf(end));
        optLogDao.delete(begin, end);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public List<String> listOptIds() {
        return optLogDao.listOptIds();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void deleteMany(Long[] logIds) {
        for (Long logId : logIds) {
            optLogDao.deleteObjectById(logId);
        }
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void save(final OperationLog optLog) {
        OptLog optlog = OptLog.valueOf(optLog);
        optlog.setLogId( optLogDao.createNewLogId());
        optLogDao.saveNewObject(optlog);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void save(List<OperationLog> optLogs) {
        List<OptLog> optlogs = new ArrayList<>(optLogs.size()+1);
        for(OperationLog ol : optLogs){
            optlogs.add(OptLog.valueOf(ol));
        }
        saveBatchObjects(optlogs);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public JSONArray listObjectsAsJson( String[] fields,
            Map<String, Object> filterMap, PageDesc pageDesc){
        //filterMap.put(CodeBook.TABLE_SORT_FIELD, "optTime");
        //filterMap.put("optId", new String[]{"login","admin","optTime"});
        return DictionaryMapUtils.mapJsonArray(
                    optLogDao.listObjectsPartFieldAsJson( filterMap, fields ,pageDesc),
                    OptLog.class);
    }


    @Override
    @Transactional
    public OptLog getObjectById(Long logId) {
        return optLogDao.getObjectById(logId);
    }


    @Override
    @Transactional
    public void deleteObjectById(Long logId) {
        optLogDao.deleteObjectById(logId);
    }

}
