package com.luwanqiang.crm.settings.service;

import com.luwanqiang.crm.exception.LoginException;
import com.luwanqiang.crm.settings.domain.User;

import java.util.Map;

public interface UserService{

    User login(User user) throws LoginException;
}
