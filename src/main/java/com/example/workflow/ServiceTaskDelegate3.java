package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ServiceTaskDelegate3 implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("ServiceTaskDelegate3 running");
		
		execution.setVariable("Result3", "Service Task 3 Completed Successfully");
	}
}
