package com.luwanqiang.crm.workbench.service;

import com.luwanqiang.crm.workbench.domain.Clue;
import com.luwanqiang.crm.workbench.domain.ClueActivityRelation;
import com.luwanqiang.crm.workbench.domain.Tran;

import java.util.List;

public interface ClueService {
    boolean save(Clue clue);

    Clue detail(String id);

    boolean unbundle(String id);

    boolean bundle(List<ClueActivityRelation> carList);

    boolean convert(String clueId, String creatBy, Tran tran);
}
