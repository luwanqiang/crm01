package com.luwanqiang.crm.workbench.service;

import com.luwanqiang.crm.vo.PaginationVO;
import com.luwanqiang.crm.workbench.domain.Activity;
import com.luwanqiang.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    boolean save(Activity activity);

    PaginationVO pageList(Map<String,Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUserListAndActivity(String id);

    boolean updateActivity(Activity activity);

    Activity detail(String id);

    List<ActivityRemark> activityRemarkList(String id);

    boolean deleteRemark(String id);

    boolean saveRemark(ActivityRemark activityRemark);

    boolean updateRemark(ActivityRemark activityRemark);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListByName(Map<String,Object> map);

    List<Activity> searchActivityListByName(String name);
}
