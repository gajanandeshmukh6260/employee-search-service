package com.happiestminds.ess.employeesearchservice.repository;

import com.happiestminds.ess.employeesearchservice.entity.EmployeeDetails;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeSearchRepository extends ElasticsearchRepository<EmployeeDetails,String> {
    @Query("{\"bool\" : {\"must\" : {\"match\" : {\"employee.employeeName\" : \"?0\"}}}}")
    List<EmployeeDetails> findEmployeeDetailsByEmployeeName(String employee);
}
