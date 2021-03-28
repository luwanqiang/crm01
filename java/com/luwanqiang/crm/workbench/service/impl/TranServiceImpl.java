package com.luwanqiang.crm.workbench.service.impl;

import com.luwanqiang.crm.utils.DateTimeUtil;
import com.luwanqiang.crm.utils.SqlSessionUtil;
import com.luwanqiang.crm.utils.UUIDUtil;
import com.luwanqiang.crm.workbench.dao.CustomerDao;
import com.luwanqiang.crm.workbench.dao.TranDao;
import com.luwanqiang.crm.workbench.dao.TranHistoryDao;
import com.luwanqiang.crm.workbench.domain.Customer;
import com.luwanqiang.crm.workbench.domain.Tran;
import com.luwanqiang.crm.workbench.domain.TranHistory;
import com.luwanqiang.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran tran, String customerName) {
        boolean flag = true;

        //根据客户名字精确查询
        Customer customer = customerDao.getByName(customerName);

        //如果查询结果为空，则新建一个客户信息
        if (customer == null){

            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setOwner(tran.getOwner());
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());

            int count1 = customerDao.save(customer);
            if (count1 != 1){
                flag = false;
            }
        }

        //上一步可获取客户ID[customerId]，信息已完善，新建交易
        tran.setCustomerId(customer.getId());
        int count2 = tranDao.save(tran);
        if (count2 != 1){
            flag = false;
        }

        //创建完交易信息后，新建一条交易记录
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());

        int count3 = tranHistoryDao.save(tranHistory);
        if (count3 != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Tran detail(String id) {

        Tran tran = tranDao.detail(id);

        return tran;
    }

    @Override
    public List<TranHistory> getTranHistoryListByTranId(String tranId) {
        System.out.println("业务层");
        List<TranHistory> tranHistoryList = tranHistoryDao.getTranHistoryListByTranId(tranId);

        return tranHistoryList;
    }

    @Override
    public boolean changeStage(Tran tran) {
        boolean flag = true;

        //更新数据
        int count1 = tranDao.changeStage(tran);
        if (count1 != 1){
            flag = false;
        }

        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateBy(tran.getEditBy());
        tranHistory.setCreateTime(tran.getEditTime());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        //创建一条交易历史
        int count2 = tranHistoryDao.save(tranHistory);
        if (count2 != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getChart() {
        //获取记录总条数
        int total = tranDao.getTotal();

        //获取每个阶段的记录条数
        List<Map<String,Object>> dataList = tranDao.getChart();

        //打包成Map返回
        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);

        return map;
    }
}
