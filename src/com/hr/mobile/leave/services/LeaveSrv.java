package com.hr.mobile.leave.services;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.utils.UserSessionUtil;
import easy.util.DateTool;
import easy.util.NumberUtils;
import org.apache.commons.lang.StringUtils;
import easy.util.UUIDTool;
import net.sf.json.JSONArray;
import sun.swing.StringUIClientPropertyKey;
import utils.bean.JsonHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaveSrv {
    /*
    增加事务
     */
    @Before(Tx.class)
    public JsonHashMap apply(Map paraMap) {
        UserSessionUtil usu = (UserSessionUtil) paraMap.get("usu");
        String date = (String) paraMap.get("date");
        String time = (String) paraMap.get("time");
        String reason = (String) paraMap.get("reason");
        Record record = new Record(); //记录分表数据
        List <Record> recordList = new ArrayList<>(); //整合分表数据
        Record r = new Record();  //记录总表数据

        String userId = usu.getUserId();

        JsonHashMap jhm = new JsonHashMap();

        JSONArray jsonArray = new JSONArray();
        jsonArray = JSONArray.fromObject(time);
        String datetime = DateTool.GetDateTime();//yyyy-MM-dd HH:mm:ss
        try {
            String leave_info_id = UUIDTool.getUUID();  //请假分表外键
            String []timeArray = new String[2];
            for(int i = 0; i < jsonArray.size(); i++){
                record = new Record();
                timeArray[0] = (String)jsonArray.getJSONObject(i).get("start");
                timeArray[1] = (String)jsonArray.getJSONObject(i).get("end");
                String sql = "select count(*) as c from h_staff_leave where staff_id=? and store_id=? and date=? and leave_start_time=? and leave_end_time=? and status='0'";
                Record countR = Db.findFirst(sql, userId, usu.getUserBean().getDeptId(), date, timeArray[0], timeArray[1]);
                Object cObj = countR.get("c");
                int c = NumberUtils.parseInt(cObj, 0);
                if (c > 0) {
                    jhm.putCode(0).putMessage("请不要重复提交数据！");
                    return jhm;
                }
                //请假分表操作
                record.set("staff_id", userId);
                record.set("date",date);
                record.set("id", UUIDTool.getUUID());
                record.set("leave_start_time", timeArray[0]);
                record.set("leave_end_time", timeArray[1]);
                record.set("leave_info_id",leave_info_id);
                recordList.add(record);
//                boolean flagL = Db.save("h_staff_leave", recordList.get(i));
//                Db.batchSave("tablename", list, list.size());
            }
            int[] flagBatchSave = Db.batchSave("h_staff_leave", recordList, recordList.size());
            //请假总表操作
            r.set("id",leave_info_id);
            r.set("staff_id", userId);
            r.set("store_id", usu.getUserBean().getDeptId());
            r.set("date", date);
            r.set("status", "0");
            r.set("reason",reason);
            r.set("creater_id", userId);
            r.set("create_time", datetime);
            r.set("modifier_id", userId);
            r.set("modify_time", datetime);
            r.set("number",jsonArray.size());
            //请求总表time字段
//            String timeSearch = "select leave_start_time as lt, leave_end_time as et from h_staff_leave where leave_info_id = ? ";
//            List <Record> timeList = Db.find(timeSearch, leave_info_id);
            r.set("times",recordList.get(0).getStr("leave_start_time")+ "-" + recordList.get(jsonArray.size()-1).getStr("leave_end_time"));

            boolean flagAll = Db.save("h_staff_leave_info", r);

            //通知表操作
            Record noticeR = new Record();
            noticeR.set("id",UUIDTool.getUUID());

            //获取员工名字添加到标题和内容中
            Record nameR = Db.findById("h_staff", userId);
            String name = nameR.getStr("name");

            noticeR.set("title",name + "的请假申请");
            noticeR.set("content",name + "的请假申请");
            noticeR.set("sender_id", userId);

            //获取该员工餐厅经理的id，job暂时不查字典值表，用store_manager代替
            String managerSearch = "select s.id as id from h_staff s where dept_id = ? and job = 'store_manager' ";
            Record managerR = Db.findFirst(managerSearch, usu.getUserBean().getDeptId());
            String managerId = managerR.getStr("id");
            noticeR.set("receiver_id", managerId);

            noticeR.set("create_time", datetime);
            noticeR.set("status","0");
            noticeR.set("type", "leave");

            //获取外键：请假表的该条数据id
            noticeR.set("fid",r.getStr("id"));
            boolean flagN = Db.save("h_notice", noticeR);

            if(flagN && flagAll){
                jhm.putCode(1).putMessage("提交成功，请等待审核！");
            } else {
                jhm.putCode(0).putMessage("提交失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jhm.putCode(-1).putMessage("服务器发生异常！");
            throw e;
        }
        return jhm;
    }


    /*
    增加事务
     */
    @Before(Tx.class)
    public JsonHashMap review(Map paraMap) {
        JsonHashMap jhm = new JsonHashMap();

        UserSessionUtil usu = (UserSessionUtil) paraMap.get("usu");
        String leaveId = (String) paraMap.get("leaveId");
        String status = (String) paraMap.get("status");
        String result = (String) paraMap.get("result");
        String dateTime = DateTool.GetDateTime();

        try {
            String sql = " select count(*) as c from h_staff_leave_info i where i.id = ? ";
            Record countL = Db.findFirst(sql, leaveId);
            if(countL.getInt("c") > 0){
                Record leaveRecord = Db.findById("h_staff_leave_info", leaveId);
                if(StringUtils.equals("0",status)){
                    leaveRecord.set("status", "1");
                } else {
                    leaveRecord.set("status","2");
                }
                leaveRecord.set("result", result);
                boolean flag = Db.update("h_staff_leave_info", leaveRecord);
                if(flag){
                    String noticeSearch = "select n.title, n.content, n.sender_id, n.receiver_id, n.type, n.fid from h_notice n where fid = ? ";
                    Record record = Db.findFirst(noticeSearch, leaveRecord.getStr("id"));
                    record.set("id", UUIDTool.getUUID());
                    String temp = record.getStr("sender_id");
                    record.set("sender_id", record.getStr("receiver_id"));
                    record.set("receiver_id", temp);
                    record.set("create_time", dateTime);
                    record.set("status","0");
                    boolean flagN = Db.save("h_notice", record);
                    if(flagN){
                        jhm.putCode(1).putMessage("审核完成！");
                    } else {
                        jhm.putCode(0).putMessage("审核失败！");
                    }
                } else {
                    jhm.putCode(0).putMessage("审核失败！");
                }
            } else {
                jhm.putCode(0).putMessage("记录不存在！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            jhm.putCode(-1).putMessage("服务器发生异常！");
        }
        return jhm;
    }
}