package com.emaf.service.component;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * S3Component
 *
 * @author khale
 * @since 2022/01/11
 */
public interface S3Component {

    /**
     * Upload file to s3
     *
     * @param directory the sub-directories is used storage upload file
     * @param mFile the file is uploaded to s3
     * @return String - public access link of file
     * @throws IOException
     * @throws URISyntaxException
     */
    String upload(String directory, MultipartFile mFile) throws IOException, URISyntaxException;

    /**
     * Upload file to s3
     *
     * @param directory the sub-directories is used storage upload file
     * @param file the file is uploaded to s3
     * @return String - public access link of file
     * @throws URISyntaxException
     */
    String upload(String directory, File file) throws URISyntaxException;

    /**
     * Upload file to s3
     *
     * @param directory the sub-directories is used storage upload file
     * @param url the url of file is uploaded to s3
     * @return String - public access link of file
     * @throws URISyntaxException
     */
    String upload(String directory, String url) throws IOException, URISyntaxException;

    /**
     * Delete file from s3
     *
     * @param directory the sub-directories is used storage upload file
     * @param fileName the file name
     * @return String - public access link of file
     * @throws IOException
     */
    void delete(String directory, String fileName);

    String get( String key);
}
