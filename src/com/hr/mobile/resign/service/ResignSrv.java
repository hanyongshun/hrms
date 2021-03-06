package com.hr.mobile.resign.service;

import com.common.service.BaseService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jiguang.JiguangPush;
import com.utils.UserSessionUtil;
import easy.util.DateTool;
import easy.util.UUIDTool;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import utils.bean.JsonHashMap;
import java.util.Map;

public class ResignSrv extends BaseService {

    public JsonHashMap apply(Map paraMap){
        JsonHashMap jhm = new JsonHashMap();
        //员工端发送离职申请，在h_notice表中添加一条记录
        String staffId = (String) paraMap.get("staffid");
        String deptId = (String) paraMap.get("deptid");
        String reason =(String)paraMap.get("reason");
        //查询所在门店经理id
        Record recordId=Db.findFirst("select s.id , s.name from h_staff s where s.dept_id=? and s.job='store_manager'",deptId);
        if(recordId==null){
            jhm.putCode(0).putMessage("部门经理不存在！");
            return jhm;
        }
        //向h_resign日志表插入一条记录
        String resignId= UUIDTool.getUUID();
        String createTime= DateTool.GetDateTime();
        Record resignRecord=new Record();
        resignRecord.set("id",resignId);
        //申请人id 传过来的staffid也是当前登录人的id
        resignRecord.set("applicant_id",staffId);
        //申请日期
        resignRecord.set("apply_time",createTime);
        //申请离职原因
        resignRecord.set("reason",reason);
        //审核人id
        resignRecord.set("reviewer_id",recordId.getStr("id"));
        //0:申请离职，1：同意离职，2：拒绝离职
        resignRecord.set("status","0");
        boolean flag1= Db.save("h_resign",resignRecord);
        //向h_notice表插入一条记录
        String id= UUIDTool.getUUID();
        String title="离职申请";
        //0为未读1为已读
        String status="0";
        //leave是请假，resign是离职
        String type="resign";
        Record recordNotice =new Record();
        recordNotice.set("id",id);
        recordNotice.set("title",title);
        recordNotice.set("content",reason);
        recordNotice.set("sender_id",staffId);
        //接收人id填写的是员工所在部门经理
        recordNotice.set("receiver_id",recordId.getStr("id"));
        recordNotice.set("create_time",createTime);
        recordNotice.set("status",status);
        recordNotice.set("type",type);
        recordNotice.set("fid",resignId);
        boolean flag2 = Db.save("h_notice", recordNotice);
        if(flag1&&flag2) {
            Record staffName = Db.findFirst("select s.name as name from h_staff s where s.id = ?",staffId);
            //发推送
            JiguangPush push = new JiguangPush("00e09994649bd900d801f6ad", "5906a375d122d73ee7cffb32");
            String tag = recordId.getStr("id");
            String []alias = {tag};
            push.setAlert("您收到了一条离职申请！(员工： " + staffName.getStr("name")  +")");
            push.setAlias(alias);
            try {
                push.sendPush();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //离职申请提交成功
            jhm.putCode(1).putMessage("提交成功！");
        }else{
            //离职申请提交失败
            jhm.putCode(0).putMessage("提交失败！");
        }
        return jhm;
    }

    public JsonHashMap review(Map paraMap) {
        JsonHashMap jhm = new JsonHashMap();
        String status = (String) paraMap.get("status");
        String reply = (String) paraMap.get("reply");
        String item = (String) paraMap.get("item");
        String noticeId = (String) paraMap.get("noticeId");
        UserSessionUtil usu = (UserSessionUtil) paraMap.get("usu");
        Record r = Db.findFirst("select n.sender_id staffId from h_notice n where n.id=?", noticeId);
        if(r==null){
            jhm.putCode(0).putMessage("记录不存在！");
            return jhm;
        }
        //获取申请离职员工id
        String staffId = r.getStr("staffId");
        //向h_notice表中添加一条记录（发给申请员工的结果通知）

        //更新h_resign日志表的员工离职申请记录
        String sqlResign = "select * from h_resign r where r.id=(select n.fid from h_notice n where n.id=?) ";
        Record record = Db.findFirst(sqlResign, noticeId);
        if (record == null) {
            jhm.putCode(0).putMessage("该条记录不存在！");
            return jhm;
        }
        //获得离职原因，下面同意离职时向h_staff_log表插入记录时desc备注
        String staffLogDesc = record.getStr("content");
        //获得到id存到h_resign_return中
        String resignId = record.getStr("id");
        String reviewTime = DateTool.GetDateTime();
        record.set("leave_date", reviewTime);
        record.set("reply", reply);
        if (StringUtils.equals(status, "0")) {
            record.set("status", "2");
        } else if (StringUtils.equals(status, "1")) {
            record.set("status", "1");
        }
        //审核人id
        record.set("reviewer_id", usu.getUserId());
        //审核时间
        record.set("review_time", reviewTime);
        Db.update("h_resign", record);
        //员工端发送离职申请，在h_notice表中添加一条记录
        Record noticeRecord = new Record();
        String id = UUIDTool.getUUID();
        noticeRecord.set("id", id);
        noticeRecord.set("title", "离职回复");
        //记录拒绝原因
        noticeRecord.set("content", reply);
        //经理id就是当前操作人（登录人）id
        noticeRecord.set("sender_id", usu.getUserId());
        noticeRecord.set("receiver_id", staffId);
        String date = DateTool.GetDateTime();
        //通知的创建时间
        noticeRecord.set("create_time", date);
        //0未读
        noticeRecord.set("status", "0");
        noticeRecord.set("type", "resign");
        noticeRecord.set("fid",resignId);
        //向notice表中添加一条记录，发送给员工通知
        Db.save("h_notice", noticeRecord);
        //向h_resign_return表中添加物品归还记录
        JSONArray timeArray = JSONArray.fromObject(item);
        for (int i = 0; i < timeArray.size(); i++) {
            String returnId = UUIDTool.getUUID();
            String value = timeArray.getJSONObject(i).getString("value");
            String returnStatus = timeArray.getJSONObject(i).getString("status");
            Record returnRecord = new Record();
            returnRecord.set("id", returnId);
            returnRecord.set("resign_id", resignId);
            //h_resign_return表中存的是value
            returnRecord.set("name", value);
            returnRecord.set("status", returnStatus);
            returnRecord.set("creater_id", usu.getUserId());
            //创建时间就是审核时间
            returnRecord.set("create_time", reviewTime);
            returnRecord.set("modifier_id", usu.getUserId());
            returnRecord.set("modify_time", reviewTime);
            Db.save("h_resign_return", returnRecord);
        }

        //推送查询名字
        //不用删除删除h_notice表中的员工本条申请离职记录
        //如果店长同意离职 还要操作：将离职员工从h_staff表中删除在h_staff_log表中添加一条记录
        if (StringUtils.equals(status, "1")) {
            String sqlStaff = "select * from h_staff s where s.id=?";
            //获取当前离职员工信息
            Record staffRecord = Db.findFirst(sqlStaff, staffId);
            if (staffRecord == null) {
                jhm.putCode(0).putMessage("该员工不存在！");
                return jhm;
            }
            //h_staff_log表中添加一条记录
            String staffLogId = UUIDTool.getUUID();
            staffRecord.set("staff_id", staffRecord.getStr("id"));
            staffRecord.set("id", staffLogId);
            //在职状态改成离职
            staffRecord.set("status","quit");
            //当前操作人id 即店长
            staffRecord.set("operater_id", usu.getUserId());
            //店长点击同意的时间即审核时间
            staffRecord.set("operate_time", reviewTime);
            //店员主动提出离职
            staffRecord.set("operate_type", "quit");
            //离职原因
            staffRecord.set("desc", staffLogDesc);
            staffRecord.remove("username");
            staffRecord.remove("password");
            Db.save("h_staff_log", staffRecord);
            //删除h_staff离职员工记录
            Db.deleteById("h_staff", staffId);
        }
        //发推送
        JiguangPush push = new JiguangPush("6863f15c5be031f95b5de21c", "130e4cbb7f9e821a26158183");
        String tag = staffId;
        String []alias = {tag};
        if (!StringUtils.equals(status, "0")) {
            push.setAlert("店长已经同意了您的离职申请！");
        } else {
            push.setAlert("店长已经拒绝了您的离职申请！");
        }
        push.setAlias(alias);
        try {
            push.sendPush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        jhm.putMessage("审核提交成功！");
        return jhm;
    }
}