package com.luwanqiang.crm.workbench.service;

import com.luwanqiang.crm.workbench.domain.Tran;
import com.luwanqiang.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    boolean save(Tran tran, String customerName);

    Tran detail(String id);

    List<TranHistory> getTranHistoryListByTranId(String tranId);

    boolean changeStage(Tran tran);

    Map<String, Object> getChart();
}
