package com.hy.salon.basic.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hy.salon.basic.common.StatusUtil;
import com.hy.salon.basic.dao.*;
import com.hy.salon.basic.entity.*;
import com.hy.salon.basic.util.UuidUtils;
import com.hy.salon.basic.vo.Result;
import com.zhxh.admin.entity.SystemUser;
import com.zhxh.admin.service.AuthenticateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hy/basic/submitApproval")
@Api(value = "SubmitApprovalController| 审核控制器")
public class SubmitApprovalController {

    @Resource(name="billTypeDAO")
    private BillTypeDAO billTypeDAO;

    @Resource(name="approvalProcessDAO")
    private ApprovalProcessDAO approvalProcessDAO;

    @Resource(name="submitApprovalDAO")
    private SubmitApprovalDAO  submitApprovalDAO;

    @Resource(name="approvalRecordDAO")
    private ApprovalRecordDAO approvalRecordDao;

    @Resource(name = "authenticateService")
    private AuthenticateService authenticateService;

    @Resource(name = "stuffDao")
    private StuffDao stuffDao;




    @ResponseBody
    @RequestMapping("/addBillType")
    @ApiOperation(value="添加单据类型", notes="添加单据类型")
    public Result addBillType(BillType condition){
        Result r=new Result();
        billTypeDAO.insert(condition);
        r.setMsgcode(StatusUtil.OK);
        r.setSuccess(true);
        return  r;
    }

    @ResponseBody
    @RequestMapping("/queryBillTypeList")
    @ApiOperation(value="单据类型列表", notes="单据类型列表")
    public Result queryBillTypeList(HttpServletRequest request){
        Result r=new Result();
        SystemUser user = authenticateService.getCurrentLogin();
        Stuff stuff = stuffDao.getStuffForUser(user.getRecordId());
        r.setTotal(approvalProcessDAO.getApprovalProcessForStore(stuff.getStoreId()).size());
        PageHelper.startPage(Integer.parseInt(request.getParameter("page")),10);
        List<BillType> billType=billTypeDAO.getAll();
        r.setData(billType);
        r.setMsgcode(StatusUtil.OK);
        r.setSuccess(true);
        return  r;
    }

    /**
     * 审核流程列表
     */
    @ResponseBody
    @RequestMapping("/queryApprovalProcessList")
    @ApiOperation(value="审核流程列表", notes="审核流程列表")
    public Result queryApprovalProcessList(HttpServletRequest request){
        Result r=new Result();
        SystemUser user = authenticateService.getCurrentLogin();
        Stuff stuff = stuffDao.getStuffForUser(user.getRecordId());
        r.setTotal(approvalProcessDAO.getApprovalProcessForStore(stuff.getStoreId()).size());
        PageHelper.startPage(Integer.parseInt(request.getParameter("page")),10);
        List<ApprovalProcess> approvalProcessList=approvalProcessDAO.getApprovalProcessForStore(stuff.getStoreId());
        r.setData(approvalProcessList);
        r.setMsgcode(StatusUtil.OK);
        r.setSuccess(true);
        return  r;
    }


    //提交审核
    @ResponseBody
    @RequestMapping("/addSubmitApproval")
    @ApiOperation(value="单据类型列表", notes="单据类型列表")
    public Result addSubmitApproval(SubmitApproval condition,Long billTypeId,Long storeId){
        Result r=new Result();

        //查找该门店，该类型的审批流程

        ApprovalProcess approvalProcess=approvalProcessDAO.getApprovalProcessForId(storeId,billTypeId);
        if(null == approvalProcess){
            r.setMsg("请先创建审批流程");
            r.setMsgcode(StatusUtil.ERROR);
            r.setSuccess(true);
            return r;
        }
        condition.setApprovalNumber(UuidUtils.generateShortUuid());
        submitApprovalDAO.insert(condition);


        ApprovalRecord approvalRecord1;
        ApprovalRecord approvalRecord2 = null;
        ApprovalRecord approvalRecord3 = null;
        ApprovalRecord approvalRecord4 = null;

        approvalRecord1=new ApprovalRecord();
        approvalRecord1.setSubmitApprovalId(condition.getRecordId());
        approvalRecord1.setApprovalStatus(new Byte("3"));
        approvalRecord1.setApprovalDate(new Date());
        approvalRecord1.setApprovalType(1);
        approvalRecord1.setSeveralApprovals(1);
        approvalRecord1.setSeveralApprovalsStuffId(approvalProcess.getFirst());
        approvalRecord1.setIsLast(1);


        if(approvalProcess.getSecond()!=null){
            approvalRecord2=new ApprovalRecord();
            approvalRecord2.setSubmitApprovalId(condition.getRecordId());
            approvalRecord2.setApprovalStatus(new Byte("2"));
            approvalRecord2.setApprovalDate(new Date());
            approvalRecord2.setApprovalType(1);
            approvalRecord2.setSeveralApprovals(2);
            approvalRecord2.setSeveralApprovalsStuffId(approvalProcess.getSecond());
            approvalRecord2.setIsLast(1);

//            if(approvalProcess.getSecond()==condition.getApprover()){
//                approvalRecord1.setApprovalStatus(new Byte("0"));
//                approvalRecord2.setApprovalStatus(new Byte("0"));
//
//            }
//
//            if(approvalProcess.getFirst()==condition.getApprover()){
//                approvalRecord1.setApprovalStatus(new Byte("0"));
//
//            }


        }else{
            approvalRecord1.setIsLast(0);
        }



        if(approvalProcess.getThird()!=null){
            approvalRecord3=new ApprovalRecord();
            approvalRecord3.setSubmitApprovalId(condition.getRecordId());
            approvalRecord3.setApprovalStatus(new Byte("2"));
            approvalRecord3.setApprovalDate(new Date());
            approvalRecord3.setApprovalType(1);
            approvalRecord3.setSeveralApprovals(3);
            approvalRecord3.setSeveralApprovalsStuffId(approvalProcess.getThird());
            approvalRecord3.setIsLast(1);
//            if(approvalProcess.getThird()==condition.getApprover()){
//                approvalRecord1.setApprovalStatus(new Byte("0"));
//                approvalRecord2.setApprovalStatus(new Byte("0"));
//                approvalRecord3.setApprovalStatus(new Byte("0"));
//
//            }
//            if(approvalProcess.getSecond()==condition.getApprover()){
//                approvalRecord3.setApprovalStatus(new Byte("3"));
//            }


        }else{
            approvalRecord2.setIsLast(0);
        }


        if(approvalProcess.getFour()!=null){
            approvalRecord4=new ApprovalRecord();
            approvalRecord4.setSubmitApprovalId(condition.getRecordId());
            approvalRecord4.setApprovalStatus(new Byte("2"));
            approvalRecord4.setApprovalDate(new Date());
            approvalRecord4.setApprovalType(1);
            approvalRecord4.setSeveralApprovals(4);
            approvalRecord4.setSeveralApprovalsStuffId(approvalProcess.getFour());
            approvalRecord4.setIsLast(0);
//            if(approvalProcess.getThird()==condition.getApprover()) {
//                approvalRecord1.setApprovalStatus(new Byte("0"));
//                approvalRecord2.setApprovalStatus(new Byte("0"));
//                approvalRecord3.setApprovalStatus(new Byte("0"));
//                approvalRecord4.setApprovalStatus(new Byte("0"));
//            }
//
//            if(approvalProcess.getThird()==condition.getApprover()){
//                approvalRecord4.setApprovalStatus(new Byte("3"));
//            }

        }else{
            approvalRecord3.setIsLast(0);
        }

        if(null!=approvalRecord1){
            approvalRecordDao.insert(approvalRecord1);
        }
        if(null!=approvalRecord2){
            approvalRecordDao.insert(approvalRecord2);
        }
        if(null!=approvalRecord3){
            approvalRecordDao.insert(approvalRecord3);
        }
        if(null!=approvalRecord4){
            approvalRecordDao.insert(approvalRecord4);
        }




        r.setMsgcode(StatusUtil.OK);
        r.setSuccess(true);
        return  r;
    }


    /**
     * 获取审批列表
     */
    @ResponseBody
    @RequestMapping("/querySubmitApproval")
    @ApiOperation(value="获取审批列表", notes="获取审批列表")
    public Result querySubmitApproval(Long stuffId,HttpServletRequest request){
        Result r=new Result();
        r.setTotal(submitApprovalDAO.getSubmitApprovalForStuff(stuffId).size());
        PageHelper.startPage(Integer.parseInt(request.getParameter("page")),10);
        List<Map<String,Object>> submitApproval=submitApprovalDAO.getSubmitApprovalForStuff(stuffId);
        r.setData(submitApproval);
        r.setMsgcode(StatusUtil.OK);
        r.setSuccess(true);
        return  r;
    }


    /**
     * 获取审批详情（申请人视角）
     */
    @ResponseBody
    @RequestMapping("/querySubmitApprovalData")
    @ApiOperation(value="获取审批详情（申请人视角）", notes="获取审批详情（申请人视角）")
    public Result querySubmitApprovalData(Long recordId,HttpServletRequest request){
        Result r=new Result();
        JSONObject jsonObj=new JSONObject();
        SubmitApproval submitApproval=submitApprovalDAO.getSubmitApprovalForId(recordId);

        ApprovalRecord ApprovalRecord=approvalRecordDao.getOneApprovalRecordForId(submitApproval.getRecordId());
        jsonObj.put("submitApproval",submitApproval);
        jsonObj.put("ApprovalRecord",ApprovalRecord);

        r.setData(jsonObj);
        r.setMsgcode(StatusUtil.OK);
        r.setSuccess(true);
        return  r;
    }


    /**
     * 获取审批请求列表
     */
    @ResponseBody
    @RequestMapping("/querySubmitRecord")
    @ApiOperation(value="获取审批请求列表", notes="获取审批请求列表")
    public Result querySubmitRecord(Long stuffId,String approvalStatus,HttpServletRequest request){
        Result r=new Result();
        r.setTotal(approvalRecordDao.getSubmitApprovalForStuff(stuffId,approvalStatus).size());
        PageHelper.startPage(Integer.parseInt(request.getParameter("page")),10);
        List<Map<String,Object>> submitApproval=approvalRecordDao.getSubmitApprovalForStuff(stuffId,approvalStatus);
        r.setData(submitApproval);
        r.setMsgcode(StatusUtil.OK);
        r.setSuccess(true);
        return  r;
    }

    /**
     * 获取审批详情（审批人视角）
     */
    @ResponseBody
    @RequestMapping("/getSubmitApprovalData")
    @ApiOperation(value="获取审批详情（申请人视角）", notes="获取审批详情（申请人视角）")
    public Result getSubmitApprovalData(Long recordId,HttpServletRequest request){
        Result r=new Result();
        JSONObject jsonObj=new JSONObject();
        SubmitApproval submitApproval=submitApprovalDAO.getSubmitApprovalForId(recordId);

        ApprovalRecord ApprovalRecord=approvalRecordDao.getOneApprovalRecordForId(submitApproval.getRecordId());
        jsonObj.put("submitApproval",submitApproval);
        jsonObj.put("ApprovalRecord",ApprovalRecord);

        r.setData(jsonObj);
        r.setMsgcode(StatusUtil.OK);
        r.setSuccess(true);
        return  r;
    }


    /**
     * 审批
     */
    @ResponseBody
    @RequestMapping("/approval")
    @ApiOperation(value="审批", notes="审批")
    public Result approval(ApprovalRecord condition,Long submitApprovalId){
        Result r=new Result();


        if(condition.getApprovalStatus()==0){
            if(condition.getIsLast()==1){
                ApprovalRecord ApprovalRecord=approvalRecordDao.getApprovalRecordForId(submitApprovalId,condition.getSeveralApprovals()+1);

                if(null!=ApprovalRecord){
                    ApprovalRecord.setApprovalStatus(new Byte("3"));
                    approvalRecordDao.update(ApprovalRecord);
                }

            }else{
                condition.setApprovalType(0);
            }
        }else{
            List<ApprovalRecord> ApprovalRecordList=approvalRecordDao.getApprovalRecordForId2(submitApprovalId,null);
            for(ApprovalRecord a:ApprovalRecordList){

                if(a.getSeveralApprovals()==1){
                    a.setApprovalStatus(new Byte("3"));
                }else{
                    a.setApprovalStatus(new Byte("2"));
                }
                approvalRecordDao.update(a);
            }

        }

        approvalRecordDao.update(condition);


//        r.setData();
        r.setMsgcode(StatusUtil.OK);
        r.setSuccess(true);
        return  r;
    }


}
