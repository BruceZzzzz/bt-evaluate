package com.btjf.controller;

import com.btjf.application.util.XaResult;
import com.btjf.common.utils.HttpClientUtil;
import com.btjf.common.utils.JSONUtils;
import com.btjf.response.UserResponse;
import com.btjf.service.EvaluateServie;
import com.btjf.vo.IndexInfoVo;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by Administrator on 2018/7/3 0003.
 */
@Api(value = "HomeController", description = "主页信息", position = 1)
@RequestMapping(value = "/api/home/")
@RestController("homeController")
public class HomeController {
    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Resource
    private EvaluateServie evaluateServie;
    @Value("call.users.url")
    private String user_url;

    /**
     * 主页信息
     *
     * @return
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public XaResult<IndexInfoVo> login(@ApiParam("中心号") String centerId, @ApiParam("窗口号") String windowId) {
        if(StringUtils.isEmpty(windowId)){
            return XaResult.error("窗口号不能为空");
        }
        if(StringUtils.isEmpty(centerId)){
            return XaResult.error("中心号不能为空");
        }
        IndexInfoVo indexInfoVo = new IndexInfoVo();

        Double avgScore = evaluateServie.averageScore("");
        Double avgTime = evaluateServie.averageTime("");
        List<String> labels = evaluateServie.getLabels("");
        String duration = null;
        if(avgTime != null && avgTime >0) {
            double min = Math.floor(avgTime / 60);
            duration = (int) min + ":" + (int) (avgTime % 60);
        }

        String result = HttpClientUtil.sendGetRequest(user_url+ centerId +"/" + windowId);
        if(StringUtils.isEmpty(result)){
            return XaResult.error("获取窗口工作人员信息失败");
        }
        LOGGER.info("获取窗口工作人员信息!返回：" + result);
        UserResponse userResponse = JSONUtils.toBean(result, UserResponse.class);
        //TODO 留待以后完善工作人员信息

        indexInfoVo.setScore((avgScore== null || avgScore <1)? "5": avgScore.toString());
        indexInfoVo.setDuration(duration);
        indexInfoVo.setLabels(labels);
        indexInfoVo.setDept(null);
        indexInfoVo.setImgs(null);
        indexInfoVo.setNotice(null);
        indexInfoVo.setName(null);
        indexInfoVo.setPicture(null);
        indexInfoVo.setWorkingYear(null);
        indexInfoVo.setStaffID(null);
        return XaResult.success(indexInfoVo);
    }




}
