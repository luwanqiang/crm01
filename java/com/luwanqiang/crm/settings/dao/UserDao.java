package com.luwanqiang.crm.settings.dao;

import com.luwanqiang.crm.settings.domain.User;

import java.util.List;

public interface UserDao {

    User login(User user);

    List<User> getUserList();

}
