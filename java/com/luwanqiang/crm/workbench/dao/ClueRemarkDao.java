package com.luwanqiang.crm.workbench.dao;

import com.luwanqiang.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {


    List<ClueRemark> getByClueId(String clueId);

    int deleteByClueId(String clueId);
}
