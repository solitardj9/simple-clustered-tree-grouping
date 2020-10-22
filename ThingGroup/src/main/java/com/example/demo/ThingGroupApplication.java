package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.demo.application.test.GroupTest;

@SpringBootApplication
public class ThingGroupApplication {

	public static void main(String[] args) {
		//
		ConfigurableApplicationContext context = SpringApplication.run(ThingGroupApplication.class, args);
		
		GroupTest groupTest = ((GroupTest)context.getBean("GroupTest"));
		
		//groupTest.doTest();
		groupTest.doInitTest();
	}

}
