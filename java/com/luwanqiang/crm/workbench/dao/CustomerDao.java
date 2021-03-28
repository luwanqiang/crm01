package com.luwanqiang.crm.workbench.dao;

import com.luwanqiang.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerDao {

    int save(Customer customer);

    Customer getByName(String company);

    List<String> getCustomerName(String name);
}
