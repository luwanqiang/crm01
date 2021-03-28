package com.luwanqiang.crm.settings.service.impl;

import com.luwanqiang.crm.exception.LoginException;
import com.luwanqiang.crm.settings.dao.UserDao;
import com.luwanqiang.crm.settings.domain.User;
import com.luwanqiang.crm.settings.service.UserService;
import com.luwanqiang.crm.utils.DateTimeUtil;
import com.luwanqiang.crm.utils.SqlSessionUtil;

import java.util.List;

public class UserServiceImpl implements UserService {

    UserDao userdao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(User user) throws LoginException {
        User loginuser = userdao.login(user);

        //验证账号密码
        if (loginuser == null){
            throw new LoginException("账号密码错误");
        }

        //验证账号有效期
        String currentTime = DateTimeUtil.getSysTime();
        String expireTime = loginuser.getExpireTime();
        if (currentTime.compareTo(expireTime)>0){
            throw new LoginException("账号已过期");
        }

        //验证账号状态
        String lockState = loginuser.getLockState();
        if ("0".equals(lockState)){
            throw new LoginException("账号已被锁定");
        }

        //验证访问IP是否合法
        String ip = user.getAllowIps();
        String allowIp = loginuser.getAllowIps();
        if (!allowIp.contains(ip)){
            throw new LoginException("IP地址不合法");
        }

        return loginuser;
    }

    @Override
    public List<User> getUserList() {

        List<User> userList = userdao.getUserList();
        return userList;

    }
}
