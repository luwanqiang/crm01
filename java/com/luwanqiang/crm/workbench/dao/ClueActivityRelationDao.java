package com.luwanqiang.crm.workbench.dao;


import com.luwanqiang.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    List<ClueActivityRelation> getByClueId(String clueId);

    int deleteByClueId(String clueId);
}
