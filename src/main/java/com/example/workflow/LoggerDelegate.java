package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class LoggerDelegate implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		System.out.println("LOG it -------------------------------------------------------- ");
	}
}
