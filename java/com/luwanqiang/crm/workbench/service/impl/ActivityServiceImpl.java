package com.luwanqiang.crm.workbench.service.impl;

import com.github.pagehelper.PageHelper;
import com.luwanqiang.crm.settings.dao.UserDao;
import com.luwanqiang.crm.settings.domain.User;
import com.luwanqiang.crm.utils.SqlSessionUtil;
import com.luwanqiang.crm.vo.PaginationVO;
import com.luwanqiang.crm.workbench.dao.ActivityDao;
import com.luwanqiang.crm.workbench.dao.ActivityRemarkDao;
import com.luwanqiang.crm.workbench.domain.Activity;
import com.luwanqiang.crm.workbench.domain.ActivityRemark;
import com.luwanqiang.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {

    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    @Override
    public boolean save(Activity activity) {
        System.out.println("进入业务层");

        boolean flag = true;
        int count = activityDao.save(activity);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }


    @Override
    public PaginationVO pageList(Map<String, Object> map) {

        //获取total
        int total = activityDao.getTotalByCondition(map);

        int pageNo = (Integer)map.get("pageNo");
        int pageSize = (Integer)map.get("pageSize");

        //PageHelper分页插件
        PageHelper.startPage(pageNo,pageSize);
        //获取市场信息列表
        List<Activity> dataList = activityDao.getActivityListByCondition(map);

        PaginationVO vo = new PaginationVO();
        vo.setTotal(total);
        vo.setDataList(dataList);

        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
        boolean flag = true;

        //查询需要删除的备注信息条数
        int selectRemarkCount = activityRemarkDao.selectCountRemarkById(ids);
        //删除信息，返回删除备注的条数
        int delectRemarkCount = activityRemarkDao.delectRemarkById(ids);

        //判断备注是否删除成功
        if (selectRemarkCount != delectRemarkCount){
            flag = false;
        }

        //删除市场活动
        int delectActivityCount = activityDao.delectActivityById(ids);

        //判断市场活动是否删除成功
        if (delectActivityCount != ids.length){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {

        UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

        //查询用户列表的id和name
        List<User> userList = userDao.getUserList();

        //根据id查询用户市场的一条记录
        Activity activity = activityDao.selectActivityById(id);

        //把结果封装到一个Map集合中
        Map<String,Object> map = new HashMap<>();
        map.put("userList",userList);
        map.put("activity",activity);
        return map;
    }

    @Override
    public boolean updateActivity(Activity activity) {
        boolean flag = true;

        int count = activityDao.updateActivity(activity);

        if (count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Activity detail(String id) {

        Activity activity = activityDao.detail(id);

        return activity;
    }

    @Override
    public List<ActivityRemark> activityRemarkList(String id) {
        System.out.println("进入备注列表控制层");

        List<ActivityRemark> activityRemarkList = activityRemarkDao.activityRemarkList(id);

        return activityRemarkList;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;

        int count = activityRemarkDao.deleteRemark(id);
        if (count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(activityRemark);

        if (count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(activityRemark);

        if (count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {

        List<Activity> activityList = activityDao.getActivityListByClueId(clueId);

        return activityList;
    }

    @Override
    public List<Activity> getActivityListByName(Map<String,Object> map) {

        List<Activity> activityList = activityDao.getActivityListByName(map);

        return activityList;
    }

    @Override
    public List<Activity> searchActivityListByName(String name) {

        List<Activity> activityList = activityDao.searchActivityListByName(name);

        return  activityList;
    }
}
