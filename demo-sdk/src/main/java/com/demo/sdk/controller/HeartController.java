package com.demo.sdk.controller;

import com.demo.sdk.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 心跳检测
 *
 * @author huangpu
 * @date 2020-01-07 10:52
 */
@RestController
public class HeartController {

    @GetMapping("/heart")
    public Result heart() {
        return Result.success();
    }
}
