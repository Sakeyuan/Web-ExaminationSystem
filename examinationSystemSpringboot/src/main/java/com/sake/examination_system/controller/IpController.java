package com.sake.examination_system.controller;

import com.sake.examination_system.util.MyResponseEntity;
import com.sake.examination_system.util.SakeUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/ip")
public class IpController {
    @GetMapping("/getIp")
    public MyResponseEntity<String> getClientIp(HttpServletRequest request) {
        return SakeUtil.getIpAddress(request);
    }

    @GetMapping("/getIp/{ip}")
    public String getClientIpByString(@PathVariable("ip") String ip) {
        return SakeUtil.getClientIpByString(ip);
    }

}
