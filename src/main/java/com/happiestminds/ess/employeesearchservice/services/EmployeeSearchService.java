package com.happiestminds.ess.employeesearchservice.services;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiest.assignment.es.entity.Employee;
import com.happiestminds.ess.employeesearchservice.config.AWSConfiguration;
import com.happiestminds.ess.employeesearchservice.entity.EmployeeDetails;
import com.happiestminds.ess.employeesearchservice.repository.EmployeeSearchRepository;
import com.happiestminds.ess.employeesearchservice.util.Constants;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class EmployeeSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeSearchService.class);

    @Autowired
    private EmployeeSearchRepository employeeSearchRepository;

    @Autowired
    private AWSConfiguration awsConfiguration;

    public EmployeeSearchService() {
    }
    public EmployeeSearchService(AWSConfiguration awsConfiguration, EmployeeSearchRepository employeeSearchRepository) {
        this.awsConfiguration=awsConfiguration;
        this.employeeSearchRepository = employeeSearchRepository;
    }

    public List<EmployeeDetails> searchEmployeesByPartialName(String fullName) {
        LOGGER.info("searchEmployeesByPartialName start service layer");
        List<EmployeeDetails> byEmployeeName = employeeSearchRepository.findEmployeeDetailsByEmployeeName(fullName);
        LOGGER.info("searchEmployeesByPartialName End service layer");
        return byEmployeeName;
    }

    @PostConstruct
    @Transactional
    public void saveEventInElasticSearch() {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(Constants.EMPLOYEE_SERVICE_QUEUE_NAME);
        String myQueueUrl = awsConfiguration.getSQSClientDetails().createQueue(createQueueRequest).getQueueUrl();
        try {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
            receiveMessageRequest.setMaxNumberOfMessages(10);
            LOGGER.info("Max No of messages : " + receiveMessageRequest.getMaxNumberOfMessages());

            AmazonSQSClient sqsClientDetails = awsConfiguration.getSQSClientDetails();
            ReceiveMessageResult receiveMessageResult = sqsClientDetails.receiveMessage(receiveMessageRequest);
            List<Message> messages = receiveMessageResult.getMessages();
            LOGGER.info("massages size : " + messages.size());
            for (Message message : messages) {
                String messageBody = message.getBody();
                JSONObject myJsonObj = (JSONObject) JSONValue.parse(messageBody);
                String messageType = (String) myJsonObj.get("Type");
                if (null == messageType) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Employee jsonObj = objectMapper.readValue(messageBody, Employee.class);
                    EmployeeDetails employeeDetails = new EmployeeDetails();
                    employeeDetails.setEmployee(jsonObj);
                    employeeSearchRepository.save(employeeDetails);
                    LOGGER.info("Employee details saved successfully ");
                }
                awsConfiguration.getSQSClientDetails().deleteMessage(myQueueUrl, message.getReceiptHandle());
                LOGGER.info("Message deleted successfully ");
            }
        } catch (Exception e) {
            LOGGER.error("Error receiving messages : ");
            e.printStackTrace();
        }
    }

    public Iterable<EmployeeDetails> getAllEmployeeDetails() {
        LOGGER.info("getAllEmployeeDetails() start");
        Iterable<EmployeeDetails> all = employeeSearchRepository.findAll();
        LOGGER.info("getAllEmployeeDetails() End");
        return all;
    }
}