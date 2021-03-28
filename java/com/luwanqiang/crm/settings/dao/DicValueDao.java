package com.luwanqiang.crm.settings.dao;

import com.luwanqiang.crm.settings.domain.DicType;
import com.luwanqiang.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getDicValueList(DicType dicType);
}
