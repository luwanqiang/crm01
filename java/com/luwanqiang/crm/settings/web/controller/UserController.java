package com.luwanqiang.crm.settings.web.controller;

import com.luwanqiang.crm.settings.domain.User;
import com.luwanqiang.crm.settings.service.UserService;
import com.luwanqiang.crm.settings.service.impl.UserServiceImpl;
import com.luwanqiang.crm.utils.MD5Util;
import com.luwanqiang.crm.utils.PrintJson;
import com.luwanqiang.crm.utils.ServiceFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到用户控制器");

        //获取<url-pattern>
        String path = request.getServletPath();


        if("/settings/user/login.do".equals(path)){

//            System.out.println(123);
            login(request,response);

        }else if("/settings/user/xxx.do".equals(path)){

            //xxx();

        }






    }

    private void login(HttpServletRequest request,HttpServletResponse response) {

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        loginPwd = MD5Util.getMD5(loginPwd);
        String ip = request.getRemoteAddr();

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        User user = new User();
        user.setLoginAct(loginAct);
        user.setLoginPwd(loginPwd);
        user.setAllowIps(ip);


        try{
            User loginuser = userService.login(user);

            request.getSession().setAttribute("user",loginuser);

            //程序执行到此说明信息验证成功，可正常登陆
            PrintJson.printJsonFlag(response,true);

        }catch(Exception e){
            e.printStackTrace();

            String msg = e.getMessage();

            Map<String,Object> map = new HashMap<>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);
        }










    }
}













