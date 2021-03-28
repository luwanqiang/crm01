package com.luwanqiang.crm.workbench.service.impl;

import com.luwanqiang.crm.utils.DateTimeUtil;
import com.luwanqiang.crm.utils.SqlSessionUtil;
import com.luwanqiang.crm.utils.UUIDUtil;
import com.luwanqiang.crm.workbench.dao.*;
import com.luwanqiang.crm.workbench.domain.*;
import com.luwanqiang.crm.workbench.service.ClueService;

import java.util.List;

public class ClueServiceImpl implements ClueService {

    //线索相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    //客户相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    //联系人相关表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    //交易相关表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);


    @Override
    public boolean save(Clue clue) {
        boolean flag = true;
        int count = clueDao.save(clue);

        if (count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Clue detail(String id) {

        Clue clue = clueDao.detail(id);

        return clue;
    }

    @Override
    public boolean unbundle(String id) {
        boolean flag = true;
        int count = clueDao.unbundle(id);

        if (count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean bundle(List<ClueActivityRelation> carList) {
        boolean flag = true;
        int count = clueDao.bundle(carList);

        if (count != carList.size()){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean convert(String clueId, String creatBy, Tran tran) {
        boolean flag = true;
        String creatTime = DateTimeUtil.getSysTime();

        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue clue = clueDao.getById(clueId);
        //--------------------------------------------------------------------------
        //经过第一步处理后，已获取[Clue]对象信息
        //--------------------------------------------------------------------------

        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户
        // （根据公司的名称精确匹配，判断该客户是否存在！）
        String company = clue.getCompany();
        Customer customer = customerDao.getByName(company);

        if (customer == null){
            customer = new Customer();

            customer.setId(UUIDUtil.getUUID());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            //tbl_customer表中的Name存放的是tbl_clue表中的company
            customer.setName(company);
            customer.setDescription(clue.getDescription());
            customer.setCreateTime(creatTime);
            customer.setCreateBy(creatBy);
            customer.setContactSummary(clue.getContactSummary());
            customer.setAddress(clue.getAddress());

            int count1 = customerDao.save(customer);
            if (count1 != 1){
                flag = false;
            }
        }
        //--------------------------------------------------------------------------
        //经过第二步处理后，已获取[Clue][Customer]对象信息
        //--------------------------------------------------------------------------

        //(3) 通过线索对象提取联系人信息，保存联系人\
        Contacts contacts = new Contacts();

        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(creatTime);
        contacts.setCreateBy(creatBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());

        int count2 = contactsDao.save(contacts);
        if (count2 != 1){
            flag = false;
        }
        //--------------------------------------------------------------------------
        //经过第三步处理后，已获取[Clue][Customer][contacts]对象信息
        //--------------------------------------------------------------------------

        //(4) 线索备注转换到客户备注以及联系人备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getByClueId(clueId);

        for(ClueRemark clueRemark : clueRemarkList) {

            String noteContent = clueRemark.getNoteContent();
            //创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setEditFlag("0");
            customerRemark.setCreateBy(creatBy);
            customerRemark.setCreateTime(creatTime);
            customerRemark.setCustomerId(customer.getId());

            int count3 = customerRemarkDao.save(customerRemark);
            if (count3 != 1) {
                flag = false;
            }

            //创建联系人备注对象，添加联系人
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setContactsId(noteContent);
            contactsRemark.setEditFlag("0");
            contactsRemark.setCreateBy(creatBy);
            contactsRemark.setCreateTime(creatTime);
            contactsRemark.setContactsId(contacts.getId());

            int count4 = contactsRemarkDao.save(contactsRemark);
            if (count4 != 1){
                flag = false;
            }
        }

        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getByClueId(clueId);

        for (ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();

            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setActivityId(clueActivityRelation.getActivityId());

            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5 != 1){
                flag = false;
            }
        }

        //(6) 如果有创建交易需求，创建一条交易
        if (tran != null){

            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(customer.getId());
            tran.setContactsId(contacts.getId());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setContactSummary(clue.getContactSummary());
            int count6 = tranDao.save(tran);
            if (count6 != 1){
                flag = false;
            }

            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();

            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setTranId(tran.getId());
            tranHistory.setStage(tran.getStage());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setCreateBy(creatBy);
            tranHistory.setCreateTime(creatTime);

            int count7 = tranHistoryDao.save(tranHistory);

            if (count7 != 1){
                flag = false;
            }
        }

        //(8) 删除线索备注
        int count8 = clueRemarkDao.deleteByClueId(clueId);

        if (count8 != 1){
            flag = false;
        }

        //(9) 删除线索和市场活动的关系
        int count9 = clueActivityRelationDao.deleteByClueId(clueId);

        if (count9 != 1){
            flag = false;
        }

        //(10) 删除线索
        int count10 = clueDao.deleteByClueId(clueId);

        if (count10 != 1){
            flag = false;
        }

        //最后返回[flag]确认是否转换完成
        return flag;
    }
}
