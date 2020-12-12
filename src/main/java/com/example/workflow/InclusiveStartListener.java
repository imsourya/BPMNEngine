package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class InclusiveStartListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution execution) throws Exception {

		System.out.println("inside Start listener");
		System.out.println(execution.toString());
		System.out.println(execution.hashCode());
		
		execution.setVariable("LeJson", "Super, I am floating Dude");
		
	}
}
