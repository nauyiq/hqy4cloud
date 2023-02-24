package com.hqy.elasticsearch.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author qy
 * @create 2021/9/8 22:47
 */
@Controller
public class IndexController {

    @GetMapping({"/index", "/"})
    public String getIndex() {
        return "index";
    }

}
