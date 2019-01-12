package com.SchoolService.SchoolServices.controller;

import java.net.URI;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class SchoolServiceController {
	private static final Logger LOG = Logger.getLogger(SchoolServiceController.class.getName());
	
	@Autowired
	private LoadBalancerClient loadBalancer;
	RestTemplate restTemplate = new RestTemplate();
	@RequestMapping(value = "/getSchoolDetails/{schoolname}", method = RequestMethod.PUT)
	//@HystrixCommand(fallbackMethod = "getStudents_Fallback")
	public String getStudents(@PathVariable String schoolname) {
		LOG.info("Inside SchoolServiceController....");
		ServiceInstance serviceInstance=loadBalancer.choose("spring-boot-zuulgatwayproxy");
		String baseUrl=serviceInstance.getUri().toString();
		LOG.info("baseUrl...."+baseUrl);
		URI storesUri = URI.create(String.format("http://%s:%s", serviceInstance.getHost(), serviceInstance.getPort()));
		LOG.info("storesUri...."+storesUri);
		String response = restTemplate.exchange(storesUri+"/student-service/getStudentDetailsForSchool/abcschool", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
			}, schoolname).getBody();
		LOG.info("response...."+response);
		return "School Name -  " + schoolname + " \n Student Details " + response;
	}
	/*@SuppressWarnings("unused")
    private String getStudents_Fallback(String schoolname) {
 
        System.out.println("Student Service is down!!! fallback route enabled...");
 
        return "CIRCUIT BREAKER ENABLED!!! No Response From Student Service at this moment. " +
                    " Service will be back shortly - " ;
    }*/

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	
}
