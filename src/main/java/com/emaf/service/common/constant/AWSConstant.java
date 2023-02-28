package com.emaf.service.common.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * AWSConstant
 *
 * @author KhaL
 * @since 2021/10/04
 */
@Getter
@Component
@PropertySource(value = "classpath:aws-${spring.profiles.active}.properties", encoding = "utf-8")
public class AWSConstant {

    /**
     * AWS
     */
    @Value("${aws.access_key}")
    private String awsAccessKey;

    @Value("${aws.secret_key}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    /**
     * AWS S3
     */
    @Value("${aws.s3.bucket}")
    private String awsS3Bucket;

    @Value("${aws.s3.temp_path}")
    private String awsS3TempPath;

}
