package com.emaf.service.controller;

import com.emaf.service.common.constant.AppConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ViewController
 *
 * @author khal
 * @since 2022/09/17
 */
@Controller
@RequestMapping(value = "/")
public class ViewController {

    @GetMapping(value = { "", "/" })
    public String getIndex() {
        return AppConstant.INDEX_TEMPLATE;
    }

    @GetMapping(value = "/single")
    public String getSingle() {
        return AppConstant.SINGLE_TEMPLATE;
    }


}
