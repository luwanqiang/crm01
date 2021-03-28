package com.luwanqiang.crm.settings.service;

import com.luwanqiang.crm.settings.domain.DicType;
import com.luwanqiang.crm.settings.domain.DicValue;

import java.util.List;
import java.util.Map;

public interface DicService {
    Map<DicType, List<DicValue>> getDicValueList();
}
