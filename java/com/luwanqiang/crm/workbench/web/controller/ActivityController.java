package com.luwanqiang.crm.workbench.web.controller;

import com.luwanqiang.crm.settings.domain.User;
import com.luwanqiang.crm.settings.service.UserService;
import com.luwanqiang.crm.settings.service.impl.UserServiceImpl;
import com.luwanqiang.crm.utils.*;
import com.luwanqiang.crm.vo.PaginationVO;
import com.luwanqiang.crm.workbench.domain.Activity;
import com.luwanqiang.crm.workbench.domain.ActivityRemark;
import com.luwanqiang.crm.workbench.service.ActivityService;
import com.luwanqiang.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/workbench/activity/getUserList.do".equals(path)){

            getUserList(response);

        }else if ("/workbench/activity/saveActivity.do".equals(path)){

            saveActivity(request,response);

        }else if ("/workbench/activity/pageList.do".equals(path)){

            pageList(request,response);

        }else if ("/workbench/activity/delete.do".equals(path)){

            delete(request,response);

        }else if ("/workbench/activity/getUserListAndActivity.do".equals(path)){

            getUserListAndActivity(request,response);

        }else if ("/workbench/activity/updateActivity.do".equals(path)){

            updateActivity(request,response);

        }else if ("/workbench/activity/detail.do".equals(path)){

            detail(request,response);

        }else if ("/workbench/activity/activityRemarkList.do".equals(path)){

            activityRemarkList(request,response);

        }else if ("/workbench/activity/deleteRemark.do".equals(path)){

            deleteRemark(request,response);

        }else if ("/workbench/activity/saveRemark.do".equals(path)){

            saveRemark(request,response);

        }else if ("/workbench/activity/updateRemark.do".equals(path)){

            updateRemark(request,response);

        }
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setId(id);
        activityRemark.setNoteContent(noteContent);
        activityRemark.setEditTime(editTime);
        activityRemark.setEditBy(editBy);
        activityRemark.setEditFlag(editFlag);

        boolean flag = activityService.updateRemark(activityRemark);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("activityRemark",activityRemark);

        PrintJson.printJsonObj(response,map);

    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {

        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "0";

        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setId(id);
        activityRemark.setNoteContent(noteContent);
        activityRemark.setCreateTime(createTime);
        activityRemark.setCreateBy(createBy);
        activityRemark.setEditFlag(editFlag);
        activityRemark.setActivityId(activityId);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.saveRemark(activityRemark);

        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("activityRemark",activityRemark);

        PrintJson.printJsonObj(response,map);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void activityRemarkList(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> activityRemarkList = activityService.activityRemarkList(id);
        PrintJson.printJsonObj(response,activityRemarkList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        String id = request.getParameter("id");

        Activity activity = activityService.detail(id);
        request.setAttribute("activity",activity);

        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }


    private void updateActivity(HttpServletRequest request, HttpServletResponse response) {

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Activity activity = new Activity();
        activity.setId(request.getParameter("id"));
        //接受的<select>标签中的owner是tbl_activity中的owner，
        // 下拉框中选项的val值存储的是tbl_user表中的id，等于tbl_activity表中的owner，
        //展示的"所有者"是通过tbl_user的id关联owner所展现的
        activity.setOwner(request.getParameter("owner"));
        activity.setName(request.getParameter("name"));
        activity.setStartDate(request.getParameter("startDate"));
        activity.setEndDate(request.getParameter("endDate"));
        activity.setCost(request.getParameter("cost"));
        activity.setDescription(request.getParameter("description"));
        activity.setEditTime(DateTimeUtil.getSysTime());
        activity.setEditBy(((User)request.getSession().getAttribute("user")).getName());

        boolean flag = activityService.updateActivity(activity);
        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        String id = request.getParameter("id");
        Map<String,Object> map = activityService.getUserListAndActivity(id);
        PrintJson.printJsonObj(response,map);

    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        String[] ids = request.getParameterValues("id");
        boolean flag = activityService.delete(ids);
        PrintJson.printJsonFlag(response,flag);
    }


    private void getUserList(HttpServletResponse response){
        UserService userService =
                (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);
    }

    private void saveActivity(HttpServletRequest request,HttpServletResponse response){

        System.out.println("进入控制层");

        ActivityService activityService = (ActivityService)
                ServiceFactory.getService(new ActivityServiceImpl());

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        String createBy =((User)request.getSession().getAttribute("user")).getName();

        Activity activity = new Activity();

        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);

        Boolean flag = activityService.save(activity);

        PrintJson.printJsonFlag(response,flag);

    }

    private void pageList(HttpServletRequest request,HttpServletResponse response){

        ActivityService activityService =
                (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNostr = request.getParameter("pageNo");
        String pageSizestr = request.getParameter("pageSize");

        int pageNo = Integer.valueOf(pageNostr);
        int pageSize = Integer.valueOf(pageSizestr);
        int skipCount = (pageNo - 1) * pageSize;

        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("pageNo",pageNo);
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);

//        PageHelper.startPage(pageNo,pageSize);
        PaginationVO vo = activityService.pageList(map);

        PrintJson.printJsonObj(response,vo);

    }

}
