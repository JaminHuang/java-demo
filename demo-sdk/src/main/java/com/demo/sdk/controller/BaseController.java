package com.demo.sdk.controller;

import com.demo.sdk.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * controller基类
 */
@Controller
@CrossOrigin
public class BaseController {

    protected Logger logger;

    // 每张excel的行数最大值
    // 此值不要乱改  2000调大的话  查询会出现溢出，  改小的话会导致查询次数过多 响应过慢
    public static final int perSheetRowCnt = Page.getMaxRow();

    public BaseController() {
        logger = LoggerFactory.getLogger(getClass());
    }
}
