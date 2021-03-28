package com.luwanqiang.crm.workbench.web.controller;

import com.luwanqiang.crm.settings.domain.DicValue;
import com.luwanqiang.crm.settings.domain.User;
import com.luwanqiang.crm.settings.service.UserService;
import com.luwanqiang.crm.settings.service.impl.UserServiceImpl;
import com.luwanqiang.crm.utils.DateTimeUtil;
import com.luwanqiang.crm.utils.PrintJson;
import com.luwanqiang.crm.utils.ServiceFactory;
import com.luwanqiang.crm.utils.UUIDUtil;
import com.luwanqiang.crm.workbench.domain.Tran;
import com.luwanqiang.crm.workbench.domain.TranHistory;
import com.luwanqiang.crm.workbench.service.CustomerService;
import com.luwanqiang.crm.workbench.service.TranService;
import com.luwanqiang.crm.workbench.service.impl.CustomerServiceImpl;
import com.luwanqiang.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/workbench/transaction/add.do".equals(path)){

            add(request,response);

        }else if ("/workbench/transaction/getCustomerName.do".equals(path)) {

           getCustomerName(request, response);

        }else if ("/workbench/transaction/save.do".equals(path)) {

            save(request, response);

        }else if ("/workbench/transaction/detail.do".equals(path)) {

            detail(request, response);

        }else if ("/workbench/transaction/getTranHistoryListByTranId.do".equals(path)) {

            getTranHistoryListByTranId(request, response);

        }else if ("/workbench/transaction/changeStage.do".equals(path)) {

            changeStage(request, response);

        }else if ("/workbench/transaction/getChart.do".equals(path)) {

            getChart(request, response);

        }
        }

    private void getChart(HttpServletRequest request, HttpServletResponse response) {

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        /*
            业务层为我们返回
                total
                dataList

                通过map打包以上两项进行返回
         */
        Map<String,Object> map = tranService.getChart();

        //扩展添加，漏斗图顶部标识
        List<String> stageList = new LinkedList<>();

        List<DicValue> dicValueList = (List<DicValue>) request.getServletContext().getAttribute("stageList");
        for (DicValue dicValue : dicValueList){
            String stage = dicValue.getValue();
            stageList.add(stage);
        }


        map.put("stageList",stageList);

        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");

        String tranId = request.getParameter("tranId");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();

        Tran tran = new Tran();
        tran.setId(tranId);
        tran.setEditBy(editBy);
        tran.setEditTime(editTime);
        tran.setExpectedDate(expectedDate);
        tran.setMoney(money);
        tran.setStage(stage);
        tran.setPossibility(pMap.get(stage));

        boolean flag = tranService.changeStage(tran);

        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("tran",tran);

        PrintJson.printJsonObj(response,map);




    }

    private void getTranHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("控制器");
        String tranId = request.getParameter("tranId");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> tranHistoryList = tranService.getTranHistoryListByTranId(tranId);

        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");

        //遍历交易历史集合，将可能性属性添加到交易历史对象中
        for (TranHistory tranHistory : tranHistoryList){

            String stage = tranHistory.getStage();

            String possibility = pMap.get(stage);

            tranHistory.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response,tranHistoryList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        //调用Service层
        Tran tran = tranService.detail(id);

        //处理阶段与可能值得对应关系
        ServletContext application = request.getServletContext();
        //ServletContext application = this.getServletContext();
        //ServletContext application = this.getServletConfig().getServletContext();

        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");

        //获取可能性值
        String stage = tran.getStage();
        String possibility = pMap.get(stage);

        //在domain中扩充一个possibility字段
        tran.setPossibility(possibility);

        request.setAttribute("tran",tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran tran = new Tran();

        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        boolean flag = tranService.save(tran,customerName);

        if (flag){
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }


    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得客户名称列表(按照客户名称进行模糊查询)");

        String name = request.getParameter("name");
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> nameList = customerService.getCustomerName(name);
        PrintJson.printJsonObj(response,nameList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到交易添加页的操作");

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        request.setAttribute("userList",userList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
