package com.example.workflow;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ConnectorTask implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		for (Map.Entry<String, Object> val: execution.getVariables().entrySet()) {
			
			System.out.println(val.getKey()+"  "+val.getValue().toString());
			
		}
		System.out.println("Connector demo finished... bye");
		
	}
}
