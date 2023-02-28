package com.emaf.service.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.emaf.service.common.constant.AWSConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWSConfig
 *
 * @author khale
 * @since 2021/11/01
 */
@Configuration
public class AWSConfig {

    private final AWSConstant awsConstant;

    public AWSConfig(final AWSConstant awsConstant) {
        this.awsConstant = awsConstant;
    }

    /**
     * AWS Credentials Configuration
     *
     * @return AWSStaticCredentialsProvider
     */
    private AWSStaticCredentialsProvider getAwsCredentials() {
        BasicAWSCredentials credentials =
                new BasicAWSCredentials(awsConstant.getAwsAccessKey(), awsConstant.getAwsSecretKey());
        return new AWSStaticCredentialsProvider(credentials);
    }

    /**
     * AWS S3 Client Configuration
     *
     * @return AmazonS3
     */
    @Bean
    public AmazonS3 s3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(getAwsCredentials())
                .withRegion(awsConstant.getAwsRegion())
                .build();
    }

}
