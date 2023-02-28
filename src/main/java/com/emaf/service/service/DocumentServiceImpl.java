package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.common.logging.AppLogger;
import com.emaf.service.component.S3Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * DocumentServiceImpl
 *
 * @author: PhuongLN
 * @since: 2022/10/09
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private S3Component s3Component;

    @Autowired
    private Message message;

    @Override
    public String uploadSamples(final MultipartFile documentFile) {
        String link = "";
        if (Objects.nonNull(documentFile)) {
            try {
                link = s3Component.upload("documents/samples", documentFile);

            } catch (IOException | URISyntaxException e) {
                AppLogger.errorLog(e.getMessage(), e);
                throw new ServerErrorException(message.getErrorUploadFileError());
            }
        }
        return link;
    }
}
