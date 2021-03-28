package com.luwanqiang.crm.workbench.dao;

import com.luwanqiang.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    int save(TranHistory tranHistory);

    List<TranHistory> getTranHistoryListByTranId(String tranId);
}
