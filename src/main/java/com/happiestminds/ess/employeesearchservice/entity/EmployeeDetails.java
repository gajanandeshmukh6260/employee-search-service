package com.happiestminds.ess.employeesearchservice.entity;

import com.happiest.assignment.es.entity.Employee;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "employeedetails")
public class EmployeeDetails {

    @Id
    private String id;
    private Employee employee;


}
