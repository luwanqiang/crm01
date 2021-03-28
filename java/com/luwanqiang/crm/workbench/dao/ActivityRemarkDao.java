package com.luwanqiang.crm.workbench.dao;

import com.luwanqiang.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int selectCountRemarkById(String[] ids);

    int delectRemarkById(String[] ids);

   List<ActivityRemark> activityRemarkList(String id);

    int deleteRemark(String id);

    int saveRemark(ActivityRemark activityRemark);

    int updateRemark(ActivityRemark activityRemark);
}
