package com.emaf.service.service;

import com.emaf.service.entity.Major;
import com.emaf.service.model.event.DocumentData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * DocumentService
 *
 * @author: PhuongLN
 * @since: 2022/10/09
 */
@Service
public interface DocumentService {


    String uploadSamples(MultipartFile documentFile);
}
