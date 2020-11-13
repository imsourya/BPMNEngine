package com.example.workflow;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

@EnableProcessApplication
public class WebappExampleProcessApplication {

	@Autowired
	  private RuntimeService runtimeService;

	  @EventListener
	  private void processPostDeploy(PostDeployEvent event) {
	    runtimeService.startProcessInstanceByKey("StartEvent_1");
	  }
}
