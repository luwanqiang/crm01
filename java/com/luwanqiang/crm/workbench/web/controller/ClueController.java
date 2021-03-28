package com.luwanqiang.crm.workbench.web.controller;

import com.luwanqiang.crm.settings.domain.User;
import com.luwanqiang.crm.settings.service.UserService;
import com.luwanqiang.crm.settings.service.impl.UserServiceImpl;
import com.luwanqiang.crm.utils.*;
import com.luwanqiang.crm.workbench.domain.Activity;
import com.luwanqiang.crm.workbench.domain.Clue;
import com.luwanqiang.crm.workbench.domain.ClueActivityRelation;
import com.luwanqiang.crm.workbench.domain.Tran;
import com.luwanqiang.crm.workbench.service.ActivityService;
import com.luwanqiang.crm.workbench.service.ClueService;
import com.luwanqiang.crm.workbench.service.impl.ActivityServiceImpl;
import com.luwanqiang.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/workbench/clue/getUserList.do".equals(path)){

            getUserList(request,response);

        }else if ("/workbench/clue/save.do".equals(path)){

            save(request,response);

        }else if ("/workbench/clue/detail.do".equals(path)){

            detail(request,response);

        }else if ("/workbench/clue/getActivityListByClueId.do".equals(path)){

            getActivityListByClueId(request,response);

        }else if ("/workbench/clue/unbundle.do".equals(path)){

            unbundle(request,response);

        }else if ("/workbench/clue/getActivityListByName.do".equals(path)){

            getActivityListByName(request,response);

        }else if ("/workbench/clue/bundle.do".equals(path)){

            bundle(request,response);

        }else if ("/workbench/clue/searchActivityListByName.do".equals(path)){

            searchActivityListByName(request,response);

        }else if ("/workbench/clue/convert.do".equals(path)){

            convert(request,response);

        }
    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("进入线索转换控制器");
        //"flag"是前端传过来的标识，用于判断是否创建交易
        String flag = request.getParameter("flag");
        String clueId = request.getParameter("clueId");
        String creatBy = ((User)request.getSession().getAttribute("user")).getName();

        Tran tran = null;
        if ("true".equals(flag)){
            tran = new Tran();
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");

            tran.setId(UUIDUtil.getUUID());
            tran.setActivityId(activityId);
            tran.setCreateBy(creatBy);
            tran.setCreateTime(DateTimeUtil.getSysTime());
            tran.setMoney(money);
            tran.setStage(stage);
            tran.setName(name);
            tran.setExpectedDate(expectedDate);

        }

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean convertFlag = clueService.convert(clueId,creatBy,tran);
        if (convertFlag){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }
    }

    private void searchActivityListByName(HttpServletRequest request, HttpServletResponse response) {

        String name = request.getParameter("name");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> activityList = activityService.searchActivityListByName(name);
        PrintJson.printJsonObj(response,activityList);
    }

    //可将两个参数直接传递到业务层，交由业务层循环执行插入语句
    private void bundle(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入bundle控制器");

        String clueId = request.getParameter("clueId");
        String[] activityIdArray = request.getParameterValues("activityId");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        List<ClueActivityRelation> carList = new LinkedList<>();

       for (int i=0; i<activityIdArray.length; i++){
           String id = UUIDUtil.getUUID();
           String activityId = activityIdArray[i];
           ClueActivityRelation clueActivityRelation = new ClueActivityRelation();

           clueActivityRelation.setId(id);
           clueActivityRelation.setClueId(clueId);
           clueActivityRelation.setActivityId(activityId);
           carList.add(clueActivityRelation);
       }

        boolean flag = clueService.bundle(carList);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入getActivityListByName控制器");

        String clueId = request.getParameter("clueId");
        String name = request.getParameter("name");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map = new HashMap<>();
        map.put("clueId",clueId);
        map.put("name",name);

        List<Activity> activityList = activityService.getActivityListByName(map);

        PrintJson.printJsonObj(response,activityList);
    }

    private void unbundle(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = clueService.unbundle(id);

        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入getActivityListByClueId控制层");

        String clueId = request.getParameter("clueId");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> activityList = activityService.getActivityListByClueId(clueId);

        PrintJson.printJsonObj(response,activityList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = clueService.detail(id);
        request.setAttribute("clue",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue clue = new Clue();
        clue.setId(id);
        clue.setOwner(owner);
        clue.setWebsite(website);
        clue.setState(state);
        clue.setSource(source);
        clue.setPhone(phone);
        clue.setNextContactTime(nextContactTime);
        clue.setMphone(mphone);
        clue.setJob(job);
        clue.setFullname(fullname);
        clue.setEmail(email);
        clue.setDescription(description);
        clue.setCreateTime(createTime);
        clue.setCreateBy(createBy);
        clue.setContactSummary(contactSummary);
        clue.setCompany(company);
        clue.setAppellation(appellation);
        clue.setAddress(address);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.save(clue);

        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("getUserList进入控制层");
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);
    }
}
