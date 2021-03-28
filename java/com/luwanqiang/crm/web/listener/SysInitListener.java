package com.luwanqiang.crm.web.listener;

import com.luwanqiang.crm.settings.domain.DicType;
import com.luwanqiang.crm.settings.domain.DicValue;
import com.luwanqiang.crm.settings.service.DicService;
import com.luwanqiang.crm.settings.service.impl.DicSerivceImpl;
import com.luwanqiang.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("监听器处理数据字典开始");

        ServletContext application = event.getServletContext();
        DicService dicService = (DicService) ServiceFactory.getService(new DicSerivceImpl());

        Map<DicType, List<DicValue>> map = dicService.getDicValueList();
        Set<DicType> dicTypeList = map.keySet();

        for (DicType key : dicTypeList){
            String type = key.getCode();
            List<DicValue> dicValueList = map.get(key);
            application.setAttribute(type+"List",dicValueList);
        }

        //阶段与可能性的对应关系
        Map<String,String> pMap = new HashMap<>();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Stage2Possibility");

        Enumeration<String> stages = resourceBundle.getKeys();

        while(stages.hasMoreElements()){
            //阶段(stage)
            String key = stages.nextElement();
            //可能性(possibility)
            String value = resourceBundle.getString(key);

            pMap.put(key,value);
        }
        application.setAttribute("pMap",pMap);
    }
}
