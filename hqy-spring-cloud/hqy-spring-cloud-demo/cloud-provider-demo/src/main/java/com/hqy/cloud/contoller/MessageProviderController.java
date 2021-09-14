package com.hqy.cloud.contoller;

import com.hqy.cloud.service.MessageProviderService;
import com.hqy.fundation.common.bind.MessageResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author qy
 * @create 2021/8/2 22:29
 */
@RestController
public class MessageProviderController {


    @Resource
    private MessageProviderService messageProviderService;

    @PostMapping("/message")
    public MessageResponse sendMessage() {
        messageProviderService.sendMessage();
        return new MessageResponse(true,"success", 0);
    }


}
