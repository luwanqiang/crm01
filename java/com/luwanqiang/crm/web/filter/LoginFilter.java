package com.luwanqiang.crm.web.filter;

import com.luwanqiang.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        User user = (User) request.getSession().getAttribute("user");

        //request.getServletPath() : /根目录下的路径
        String path = request.getServletPath();
        if ("/settings/user/login.do".equals(path) || "/login.jsp".equals(path)){

            filterChain.doFilter(servletRequest,servletResponse);

        }else{

            if (user == null){
                //request.getContextPath() : 获取的是Tomcat发布配置的项目名 /crm
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }else{
                filterChain.doFilter(servletRequest,servletResponse);
            }

        }



    }
}
