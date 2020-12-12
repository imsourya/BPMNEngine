package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class InclusiveEndListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		System.out.println("inside End listener");
		
		System.out.println("FROM Service 1 = " + execution.getVariable("Result1"));
		System.out.println("FROM Service 2 = " + execution.getVariable("Result2"));
		System.out.println("FROM Service 3 = " + execution.getVariable("Result3"));
	}
}
