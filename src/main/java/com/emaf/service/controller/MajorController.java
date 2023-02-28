package com.emaf.service.controller;

import com.emaf.service.entity.Major;
import com.emaf.service.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RoomController
 *
 * @author: PhuongLN
 * @since: 2022/10/09
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/major")
public class MajorController {

    @Autowired
    private MajorService majorService;

    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Major> getAllMajor() {
        return majorService.getAllMajor();
    }
    
}
