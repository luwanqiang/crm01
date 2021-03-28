package com.luwanqiang.crm.workbench.service.impl;

import com.luwanqiang.crm.utils.SqlSessionUtil;
import com.luwanqiang.crm.workbench.dao.CustomerDao;
import com.luwanqiang.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getCustomerName(String name) {

        List<String> nameList = customerDao.getCustomerName(name);

        return nameList;
    }
}
