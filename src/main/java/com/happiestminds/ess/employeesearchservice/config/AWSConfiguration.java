package com.happiestminds.ess.employeesearchservice.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AWSConfiguration {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;

    public AWSConfiguration(String accessKey, String secretKey, String region) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
    }
    public AWSConfiguration(){}

    @Primary
    @Bean
    public AmazonSQSClient getSQSClientDetails() {
        return (AmazonSQSClient) AmazonSQSClientBuilder.standard().withRegion("ap-south-1")
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey,
                        secretKey)))
                .build();
    }
}
