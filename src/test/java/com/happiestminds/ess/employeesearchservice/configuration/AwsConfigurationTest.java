package com.happiestminds.ess.employeesearchservice.configuration;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.happiestminds.ess.employeesearchservice.config.AWSConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AwsConfigurationTest {

    private AWSConfiguration awsConfiguration;


    @Before
    public void setUp() {
        String accessKey="accessKey";
        String secretKey="secretKey";
        String region="region";
        awsConfiguration = new AWSConfiguration(accessKey, secretKey, region);
    }


    @Test
    public void TestGetSQSClientDetails(){
        AmazonSQSClientBuilder sqsClientBuilder = mock(AmazonSQSClientBuilder.class);
        AmazonSQSClient sqsClient = mock(AmazonSQSClient.class);
        when(sqsClientBuilder.withRegion("ap-south-1")).thenReturn(sqsClientBuilder);

        when(sqsClientBuilder.withCredentials(any(AWSStaticCredentialsProvider.class))).thenReturn(sqsClientBuilder);
        when(sqsClientBuilder.build()).thenReturn(sqsClient);
        awsConfiguration.getSQSClientDetails();
    }

}
