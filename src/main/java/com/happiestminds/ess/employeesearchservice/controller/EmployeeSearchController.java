package com.happiestminds.ess.employeesearchservice.controller;

import com.happiestminds.ess.employeesearchservice.entity.EmployeeDetails;
import com.happiestminds.ess.employeesearchservice.services.EmployeeSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ESS")
public class EmployeeSearchController {

    public static final Logger LOGGER= LoggerFactory.getLogger(EmployeeSearchController.class);

    @Autowired
    EmployeeSearchService employeeSearchService;

    @GetMapping("/search/{name}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<EmployeeDetails> searchEmployeesByPartialName(@PathVariable String name){
        LOGGER.info("Employee search details for : "+name);
        List<EmployeeDetails> employeeDetailsList = employeeSearchService.searchEmployeesByPartialName(name);
        employeeDetailsList.stream().map(p->p.getEmployee()).forEach(p->LOGGER.info("Employee Name : "+p.getEmployeeName()));
        LOGGER.info("Employee search details Fetched End");
        return employeeDetailsList;
    }

    @GetMapping("/allEmployeeDetails")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Iterable<EmployeeDetails> getAllEmployeeDetails(){
        LOGGER.info("All Employee Details fetching Started");
        Iterable<EmployeeDetails> employeeDetailsList = employeeSearchService.getAllEmployeeDetails();
        LOGGER.info("All Employee Details fetching Ended");
        return employeeDetailsList;
    }
}
