package com.luwanqiang.crm.settings.service.impl;

import com.luwanqiang.crm.settings.dao.DicTypeDao;
import com.luwanqiang.crm.settings.dao.DicValueDao;
import com.luwanqiang.crm.settings.domain.DicType;
import com.luwanqiang.crm.settings.domain.DicValue;
import com.luwanqiang.crm.settings.service.DicService;
import com.luwanqiang.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicSerivceImpl implements DicService {
    DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<DicType, List<DicValue>> getDicValueList() {
        Map<DicType,List<DicValue>> map = new HashMap<>();

        List<DicType> typeList = dicTypeDao.getDicTypeList();

        for (DicType dicType : typeList){
            List<DicValue> valueList = dicValueDao.getDicValueList(dicType);
            map.put(dicType,valueList);
        }

        return map;
    }
}
