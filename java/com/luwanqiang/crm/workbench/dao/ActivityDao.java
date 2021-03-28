package com.luwanqiang.crm.workbench.dao;

import com.luwanqiang.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
    int save(Activity activity);

    int getTotalByCondition(Map<String,Object> map);

    List<Activity> getActivityListByCondition(Map<String, Object> map);

    int delectActivityById(String[] ids);

    Activity selectActivityById(String id);

    int updateActivity(Activity activity);

    Activity detail(String id);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListByName(Map<String,Object> map);

    List<Activity> searchActivityListByName(String name);
}
