package com.hqy.cloud.contoller;

import com.hqy.base.common.bind.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qy
 * @create 2021/7/28 22:09
 */
@RestController
@RequestMapping("/node")
@RefreshScope
public class NodeInfoController {

//    @Value("${env}")
//    private String env;

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name}")
    private String name;



    @GetMapping(value = "/serverInfo")
    public MessageResponse getEnvInfo(){
        return new MessageResponse(true, "serverName:" + name + " port:" + port , 0);
    }



}
