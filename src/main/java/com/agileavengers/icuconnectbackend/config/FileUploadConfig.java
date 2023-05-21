package com.agileavengers.icuconnectbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class FileUploadConfig {
    @Value("${config.aws.s3.access-key}")
    private String accessKey;

    @Value("${config.aws.s3.secret-key}")
    private String accessSecret;

    @Value("${config.aws.region}")
    private String region;

    @Value("${config.aws.s3.url}")
    private String s3EndpointUrl;

    @Bean
    public AmazonS3 s3Client() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(getCredentialsProvider())
                .withEndpointConfiguration(getEndpointConfiguration(s3EndpointUrl))
                .enablePathStyleAccess()
                .build();
    }

    private EndpointConfiguration getEndpointConfiguration(String url) {
        return new EndpointConfiguration(url, region);
    }

    private AWSStaticCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(getBasicAWSCredentials());
    }

    private BasicAWSCredentials getBasicAWSCredentials() {
        return new BasicAWSCredentials(accessKey, accessSecret);
    }

}
