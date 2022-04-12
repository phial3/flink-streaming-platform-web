package com.flink.streaming.web.service;

import com.flink.streaming.web.base.TestRun;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 01:29
 */
public class TestSystemConfigService extends TestRun {

    @Resource
    private SystemConfigService systemConfigService;



}
