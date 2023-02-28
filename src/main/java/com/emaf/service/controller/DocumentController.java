package com.emaf.service.controller;

import com.emaf.service.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * DocumentController
 *
 * @author: PhuongLN
 * @since: 2022/12/12
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping(value = "/uploadSample", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadSample(@RequestParam(value = "documentFile") MultipartFile documentFile){
        return documentService.uploadSamples(documentFile);
    }
}