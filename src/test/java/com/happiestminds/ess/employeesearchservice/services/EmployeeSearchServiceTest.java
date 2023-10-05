package com.happiestminds.ess.employeesearchservice.services;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiest.assignment.es.awsconfig.AWSConfig;
import com.happiest.assignment.es.entity.Address;
import com.happiest.assignment.es.entity.Department;
import com.happiest.assignment.es.entity.Employee;
import com.happiest.assignment.es.util.Constants;
import com.happiestminds.ess.employeesearchservice.config.AWSConfiguration;
import com.happiestminds.ess.employeesearchservice.entity.EmployeeDetails;
import com.happiestminds.ess.employeesearchservice.repository.EmployeeSearchRepository;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EmployeeSearchServiceTest {

    //private AWSConfiguration awsConfiguration;

    @Mock
    private EmployeeSearchRepository employeeSearchRepository;

    @InjectMocks
    private EmployeeSearchService employeeSearchService;

    @Mock
    AmazonSQSClient amazonSQSClient;

    @Mock
    ReceiveMessageResult receiveMessageResult;

    @Mock
    CreateQueueResult createQueueResult;

    @Mock
    private AWSConfiguration awsConfiguration;

    @Test
    public void testSearchEmployeesByPartialName() {
        String partialName = "abc";
        List<EmployeeDetails> employeeDetailsList = getEmployeeDetailsList();

        when(employeeSearchRepository.findEmployeeDetailsByEmployeeName(partialName)).thenReturn(employeeDetailsList);
        List<EmployeeDetails> result = employeeSearchService.searchEmployeesByPartialName(partialName);
        assertEquals(employeeDetailsList.size(), result.size());
    }

    private List<EmployeeDetails> getEmployeeDetailsList() {
        List<EmployeeDetails> mockEmployeesList = new ArrayList<>();
        EmployeeDetails employee1 = new EmployeeDetails();
        employee1.setId("Ef223ft3");
        employee1.setEmployee(getEmployee());
        mockEmployeesList.add(employee1);

        return mockEmployeesList;
    }

    @Test
    public void testGetAllEmployeeDetails() {
        List<EmployeeDetails> employeeDetailsList = getEmployeeDetailsList();
        employeeDetailsList.forEach(employeeDetails -> employeeDetails.getEmployee());
        Mockito.when(employeeSearchRepository.findAll()).thenReturn(employeeDetailsList);
        Iterable<EmployeeDetails> result = employeeSearchService.getAllEmployeeDetails();

        List<EmployeeDetails> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertEquals(employeeDetailsList.size(), resultList.size());
    }

    @Test
    public void testSaveEventInElasticSearch() throws JSONException {
        String queueName = "employee-service-sqs";
        String messageBody1="{\"id\" : 1,\"employeeName\" : \"Abc\",\"mailId\" : \"abc@gmail.com\",\"mobileNumber\" : \"9876543210\",\"department\" : [ {\"id\" : 1,\"departmentName\" : \"Development\"}, {\"id\" : 2,\"departmentName\" : \"Testing\"} ],\"address\" : [ {\"id\" : 1,\"addressDetails\" : \"SB Road\"}, {\"id\" : 2,\"addressDetails\" : \"FC Road\"} ]}\n";
        String messageBody2="{\"id\" : 2,\"employeeName\" : \"Abc\",\"mailId\" : \"abc@gmail.com\",\"mobileNumber\" : \"9876543210\",\"department\" : [ {\"id\" : 11,\"departmentName\" : \"Development\"}, {\"id\" : 12,\"departmentName\" : \"Testing\"} ],\"address\" : [ {\"id\" : 11,\"addressDetails\" : \"SB Road\"}, {\"id\" : 12,\"addressDetails\" : \"FC Road\"} ]}\n";

        Message tempMessage1 = new Message()
                .withMessageId("MessageId1")
                .withBody(messageBody1);
        Message tempMessage2 = new Message()
                .withMessageId("MessageId2")
                .withBody(messageBody2);
        String queueUrl = "URL";

        ReceiveMessageRequest receiveMessageRequest = mock(ReceiveMessageRequest.class);
        receiveMessageRequest.setQueueUrl(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        EmployeeDetails employeeDetails=new EmployeeDetails();
        employeeDetails.setId("ABCD");
        employeeDetails.setEmployee(getEmployee());

        CreateQueueRequest createQueueRequest = new CreateQueueRequest(Constants.EMPLOYEE_SERVICE_QUEUE_NAME);

        when(awsConfiguration.getSQSClientDetails()).thenReturn(amazonSQSClient);
        when(createQueueResult.getQueueUrl()).thenReturn("test");
        when(amazonSQSClient.createQueue(createQueueRequest)).thenReturn(createQueueResult);

        List<Message> messages = new ArrayList<>();
        messages.add(tempMessage1);
        messages.add(tempMessage2);
        receiveMessageResult.setMessages(Arrays.asList(tempMessage1, tempMessage2));

        when(amazonSQSClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(receiveMessageResult);

        assertEquals(2, messages.size());
        assertEquals(messageBody1, messages.get(0).getBody());
        assertEquals(messageBody2, messages.get(1).getBody());

        when(receiveMessageResult.getMessages()).thenReturn(messages);

        JSONObject jsonObject=(JSONObject) JSONValue.parse(messageBody1);
        JSONAssert.assertEquals(messageBody1, jsonObject.toJSONString(),true);

        when(employeeSearchRepository.save(any(EmployeeDetails.class))).thenReturn(employeeDetails);
      //  Mockito.when(employeeSearchRepository.save(employeeDetails)).thenReturn(employeeDetails);
        employeeSearchService.saveEventInElasticSearch();

        verify(amazonSQSClient).createQueue(any(CreateQueueRequest.class));
        verify(amazonSQSClient).receiveMessage(any(ReceiveMessageRequest.class));
        Assertions.assertEquals("ABCD", employeeDetails.getId());
    }


    private Employee getEmployee() {
        List<Department> listOfDepartments = getListOfDepartments();
        List<Address> listAddress = getListAddress();

        Employee employee = new Employee();
        employee.setId(1);
        employee.setEmployeeName("Abc");
        employee.setMobileNumber("9876543210");
        employee.setMailId("abc@gmail.com");
        employee.setAddress(listAddress);
        employee.setDepartment(listOfDepartments);
        return employee;
    }

    private List<Department> getListOfDepartments() {
        List<Department> departments = new ArrayList<>();
        departments.add(new Department(1, "Development"));
        departments.add(new Department(2, "Testing"));
        return departments;
    }

    private List<Address> getListAddress() {
        List<Address> lstAddress = new ArrayList<>();
        lstAddress.add(new Address(1, "SB Road"));
        lstAddress.add(new Address(2, "FC Road"));
        return lstAddress;
    }
}


