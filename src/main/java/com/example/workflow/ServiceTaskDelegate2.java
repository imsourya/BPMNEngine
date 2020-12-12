package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ServiceTaskDelegate2 implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("ServiceTaskDelegate2 running");
		
		execution.setVariable("Result2", "Service Task 2 Completed Successfully");
	}
}
