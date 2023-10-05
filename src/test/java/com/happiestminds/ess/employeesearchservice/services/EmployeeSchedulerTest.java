package com.happiestminds.ess.employeesearchservice.services;

import com.happiestminds.ess.employeesearchservice.repository.EmployeeSearchRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EmployeeSchedulerTest {


    @InjectMocks
    private EmployeeScheduledService employeeScheduledService;

    @Mock
    private EmployeeSearchRepository employeeSearchRepository;

    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    public void setUp(){
        employeeScheduledService=new EmployeeScheduledService(restTemplate,employeeSearchRepository);
    }

    @Test
    public void testCountOfEmployeeScheduler(){

        long countOfESS = 10L;
        long countOfES = 10L;

        when(employeeSearchRepository.count()).thenReturn(countOfESS);
        when(employeeScheduledService.callExternalService()).thenReturn(countOfES);
        employeeScheduledService.countOfEmployeeSechedular();
        verify(employeeSearchRepository).count();

    }

    @Test
    public void testCountOfEmployeeSchedulerWithFallback() {
        long countOfESS = 10L;
        long countOfES = 0L;

        when(employeeSearchRepository.count()).thenReturn(countOfESS);
        when(employeeScheduledService.callExternalService()).thenReturn(countOfES);
        employeeScheduledService.countOfEmployeeSechedular();
        verify(employeeSearchRepository).count();
    }

    @Test
    public void testFallBackMethod(){
        Long result = employeeScheduledService.fallBackMethod();
        assertEquals(Long.valueOf(0L),result);
    }
}



