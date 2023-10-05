package com.happiestminds.ess.employeesearchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableScheduling
@EnableEurekaClient
@EnableHystrix
@EnableJpaRepositories(basePackages = "com.happiestminds.ess.employeesearchservice.repository")
@ComponentScan(basePackages ={ "com.happiestminds.ess.employeesearchservice.services"
		,"com.happiestminds.ess.employeesearchservice.config"
		,"com.happiestminds.ess.employeesearchservice.filter"
		,"com.happiestminds.ess.employeesearchservice.controller"})
public class EmployeeSearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeSearchServiceApplication.class, args);
	}

}
