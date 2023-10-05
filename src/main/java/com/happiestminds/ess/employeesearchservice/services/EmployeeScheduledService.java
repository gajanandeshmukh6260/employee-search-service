package com.happiestminds.ess.employeesearchservice.services;

import com.happiestminds.ess.employeesearchservice.repository.EmployeeSearchRepository;
import com.happiestminds.ess.employeesearchservice.util.Constants;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class EmployeeScheduledService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeScheduledService.class);

    private final RestTemplate restTemplate;

    @Autowired
    private EmployeeSearchRepository employeeSearchRepository;

    public EmployeeScheduledService(RestTemplate restTemplate,EmployeeSearchRepository employeeSearchRepository) {
        this.restTemplate = restTemplate;
        this.employeeSearchRepository=employeeSearchRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    public void countOfEmployeeSechedular() {
        long countOfESS = employeeSearchRepository.count();
        Long countOfES = callExternalService();
        if (countOfES == 0L) {
            LOGGER.info("Fallback response : Employee-service unavailable. please try again later... ");
        } else {
            if (countOfES.equals(countOfESS)) {
                LOGGER.info("Count of ES : " + countOfES + " and count of ESS : " + countOfESS);
                LOGGER.info("Records synced at Time : " + new Date());
            } else {
                LOGGER.info("Count of ES : " + countOfES + " and count of ESS : " + countOfESS);
                LOGGER.info("Records not synced..." + new Date());
            }
        }
    }

    @HystrixCommand(fallbackMethod = "fallBackMethod")
    public Long callExternalService() {
        return restTemplate.getForObject(Constants.EMPLOYEE_SERVICE_COUNT_API, long.class);
    }

    public Long fallBackMethod() {
        return 0L;
    }

}
