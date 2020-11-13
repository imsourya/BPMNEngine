package com.example.workflow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnector;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnectorId;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputOutput;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOutputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaScript;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class);
    //addConnectorToServiceTask();
  }
  
  static void addConnectorToServiceTask() {
		
		BpmnModelInstance modelInstance = Bpmn
				.createExecutableProcess("invoice")
				.startEvent("start")
					.name("Start")
				.serviceTask("task1")
					.name("Task 1")
					.camundaResultVariable("response")
				.serviceTask("task2")
					.name("Task 2")
					.camundaClass(ConnectorTask.class)  // dummy class
				.endEvent()
				.done();
		
		CamundaConnector connector = modelInstance
				.newInstance(CamundaConnector.class);
		CamundaConnectorId connectorId = modelInstance
				.newInstance(CamundaConnectorId.class);
		CamundaInputOutput inputoutput = modelInstance
				.newInstance(CamundaInputOutput.class);
		
		CamundaInputParameter url = modelInstance
				.newInstance(CamundaInputParameter.class); 
		CamundaInputParameter method = modelInstance
				.newInstance(CamundaInputParameter.class); 
		CamundaOutputParameter response = modelInstance
				.newInstance(CamundaOutputParameter.class);
		CamundaScript script = modelInstance
				.newInstance(CamundaScript.class);
		
		
		url.setAttributeValue("name", "url");
		url.setTextContent("https://jsonplaceholder.typicode.com/posts/1");
		inputoutput.getCamundaInputParameters().add(url);
		
		method.setAttributeValue("name", "method");
		method.setTextContent("GET");		
		inputoutput.getCamundaInputParameters().add(method);
		
		response.setAttributeValue("name", "response");
		script.setCamundaScriptFormat("JavaScript");
		script.setTextContent("S(response).prop(\"title\")");
		response.setValue(script);
		inputoutput.getCamundaOutputParameters().add(response);
		
		connectorId.setTextContent("http-connector");
		connector.setCamundaConnectorId(connectorId);
		connector.setCamundaInputOutput(inputoutput);  
		
		ServiceTask serviceTask = modelInstance.getModelElementById("task1");
		serviceTask.builder().addExtensionElement(connector);
		
		Bpmn.writeModelToStream(System.out, modelInstance);
		
		//repositoryService.createDeployment().addModelInstance("invoice.bpmn", modelInstance)
		//		.deploy();
		File file;
		try {
			file = File.createTempFile("bpmn-connector", ".bpmn");
			Bpmn.writeModelToFile(file, modelInstance);
			System.out.println(file.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}