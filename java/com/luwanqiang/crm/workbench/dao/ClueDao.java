package com.luwanqiang.crm.workbench.dao;


import com.luwanqiang.crm.workbench.domain.Clue;
import com.luwanqiang.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueDao {


    int save(Clue clue);

    Clue detail(String id);

    int unbundle(String id);

    int bundle(List<ClueActivityRelation> carList);

    Clue getById(String clueId);

    int deleteByClueId(String clueId);
}
