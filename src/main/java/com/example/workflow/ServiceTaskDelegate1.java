package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ServiceTaskDelegate1 implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("ServiceTaskDelegate1 running");
		
		execution.setVariable("Result1", "Service Task 1 Completed Successfully");
	}

}
