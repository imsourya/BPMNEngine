package com.example.workflow;

import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN20_NS;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_NS;

import java.io.File;
import java.io.IOException;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnector;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnectorId;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputOutput;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOutputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaScript;

public class BpmnFluentAPI {
	
	protected static <T extends BpmnModelElementInstance> T 
		createElement(BpmnModelElementInstance parentElement, 
				String id, Class<T> elementClass) {
	  T element = parentElement.getModelInstance().newInstance(elementClass);
	  element.setAttributeValue("id", id, true);
	  //e.addChildElement(element);
	  parentElement.addChildElement(element);
	  return element;
	}
	
	static void createAllPossibleTask() throws IOException {
		BpmnModelInstance modelInstance = Bpmn.createProcess()
				.name("sample")
				.startEvent("start")
				.userTask("usertask")
				.parallelGateway("fork")
				.serviceTask("servicetask")
				.parallelGateway("join")
				.moveToNode("fork")
				.serviceTask("svctask02")
				.connectTo("join")
				.condition("condition01", "#{fine}")
				.camundaAsyncAfter(true)
				.businessRuleTask("br01")
				.callActivity("callActvity01")
				.manualTask("manualTask01")
				.subProcess("subProcess01")
				.scriptTask("scriptTask01")
				.endEvent("end")
				.done();

		System.out.println(Bpmn.convertToString(modelInstance));
		
//		BpmnModelInstance modelInstance2 = Bpmn.createEmptyModel();
//		Definitions definitions = modelInstance2.newInstance(Definitions.class);
//		definitions.setTargetNamespace("http://wf.ey.com/examples");
//		modelInstance2.setDefinitions(definitions);
//
//		Process process = createElement(definitions, "process", Process.class);
//		//process.setId("process");
//
//		// create start event, user task and end event
//		StartEvent startEvent = createElement(process, "start", StartEvent.class);
//		UserTask task1 = createElement(process, "task1", UserTask.class);
//		task1.setName("User Task");
//		EndEvent endEvent = createElement(process, "end", EndEvent.class);
//
//		// create the connections between the elements
//		createSequenceFlow(process, startEvent, task1);
//		createSequenceFlow(process, task1, endEvent);		
//		
//		definitions.addChildElement(process);
//
//		Bpmn.validateModel(modelInstance2);
//		System.out.println(Bpmn.convertToString(modelInstance2));
//		File file = File.createTempFile("bpmn-model-api-", ".bpmn");
		File file = File.createTempFile("bpmn-model-api-", ".bpmn");
		Bpmn.writeModelToFile(file, modelInstance);
		System.out.println(file.getAbsolutePath());		
	}
	
	static void createLoopedTask() throws IOException {
		BpmnModelInstance modelInstance = Bpmn.createProcess()
				  .startEvent()
				  .userTask()
				  .id("question")
				  .exclusiveGateway("status")
				  .name("Everything fine?")
				    .condition("yes", "#{fine}")
				    .serviceTask()
				    .userTask()
				    .endEvent()
				  .moveToLastGateway()
				    .condition("no", "#{!fine}")
				    .userTask()
				    .connectTo("status")
				  .done();
		File file = File.createTempFile("bpmn-model-api-", ".bpmn");
		Bpmn.writeModelToFile(file, modelInstance);
		System.out.println(file.getAbsolutePath());
		
	}
	
	static void createMultiInstanceWithTransactionBoundary() throws IOException {
		BpmnModelInstance modelInstance = 
			Bpmn.createProcess()
			  .startEvent()
			  .serviceTask("servicetask")
			    .camundaAsyncBefore() // multi-instance body
			    .multiInstance()
			      .camundaAsyncBefore() // every instance
			      .parallel()
			    .multiInstanceDone()
			  .endEvent()
			  .done();		

		File file = File.createTempFile("bpmn-model-api-", ".bpmn");
		Bpmn.writeModelToFile(file, modelInstance);
		System.out.println(file.getAbsolutePath());
	}
	
	static void updateModel() throws IOException {
		BpmnModelInstance modelInstance = Bpmn.createExecutableProcess()
				.startEvent()
				.userTask("task1")
				.businessRuleTask("br01")
				.endEvent()
				.done();
				// Bpmn.readModelFromFile(new File("PATH/TO/MODEL.bpmn"));
		BusinessRuleTask brTask = (BusinessRuleTask) modelInstance.getModelElementById("br01");
		brTask.builder().camundaDecisionRef("dmn01");

//		SequenceFlow outgoingSequenceFlow = brTask.getOutgoing().iterator().next();
//		FlowNode svcTask = outgoingSequenceFlow.getTarget();
//		brTask.getOutgoing().remove(outgoingSequenceFlow);		
		File file = File.createTempFile("bpmn-model-api-", ".bpmn");
		Bpmn.writeModelToFile(file, modelInstance);
		System.out.println(file.getAbsolutePath());
	}
	
	static void addGetHTTPConnectorToServiceTask() {

		BpmnModelInstance modelInstance = Bpmn
				.createExecutableProcess("invoice")
				.startEvent("start")
				.name("Start")
				.serviceTask("task1")
				.name("Task 1")
				.camundaResultVariable("response")
				.serviceTask("task2")
				.name("Task 2")
				.camundaClass("com.example.workflow.ConnectorTask")
				.endEvent()
				.done();
        ////// Connector code
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
		url.setTextContent("https://jsonplaceholder.typicode.com/posts/1");     // API added
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

		//// Add Connector to task
		ServiceTask serviceTask = modelInstance.getModelElementById("task1");
		serviceTask.builder().addExtensionElement(connector);

		Bpmn.writeModelToStream(System.out, modelInstance);
	}
	
	public static void connect(SequenceFlow flow, FlowNode from, FlowNode to) {
		  flow.setSource(from);
		  from.getOutgoing().add(flow);
		  flow.setTarget(to);
		  to.getIncoming().add(flow);
		}
	
	static void newCode() {
		BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
		  Definitions definitions = modelInstance.newInstance(Definitions.class);
		  definitions.setTargetNamespace("https://camunda.org/examples");
		  modelInstance.setDefinitions(definitions);

		  Process process = modelInstance.newInstance(Process.class);
		  definitions.addChildElement(process);

		  StartEvent startEvent = modelInstance.newInstance(StartEvent.class);
		  startEvent.setId("start");
		  process.addChildElement(startEvent);

		  UserTask userTask = modelInstance.newInstance(UserTask.class);
		  userTask.setId("task");
		  userTask.setName("User Task");
		  process.addChildElement(userTask);

		  SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		  sequenceFlow.setId("flow1");
		  process.addChildElement(sequenceFlow);
		  connect(sequenceFlow, startEvent, userTask);

		  EndEvent endEvent = modelInstance.newInstance(EndEvent.class);
		  endEvent.setId("end");
		  process.addChildElement(endEvent);

		  sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		  sequenceFlow.setId("flow2");
		  process.addChildElement(sequenceFlow);
		  connect(sequenceFlow, userTask, endEvent);

		  Bpmn.writeModelToStream(System.out, modelInstance);
	}
	
	static void addInclusiveGateWay() {

		/*BpmnModelInstance modelInstance = Bpmn
				.createExecutableProcess("invoice")
				.startEvent("start")
				.name("Start")
				.inclusiveGateway("start").endEvent().done();*/
		
		BpmnModelInstance modelInstance = Bpmn.createProcess()
				  .name("BPMN API Invoice Process")
				  .startEvent("start")
					.name("Start")
				  .inclusiveGateway()
				    .name("Invoice approved?")
				    .id("start1")
				    .gatewayDirection(GatewayDirection.Diverging)
				  .condition("yes", "${approved}")
				  .userTask()
				    .name("Prepare Bank Transfer")
				    .camundaCandidateGroups("accounting")
				  .serviceTask().sequenceFlowId("dffd")
				    .name("Archive Invoice")
				    .camundaClass("org.camunda.bpm.example.invoice.service.ArchiveInvoiceService")
				  .inclusiveGateway("end")
				    .name("end gateway")
				    .id("end1")
				    .gatewayDirection(GatewayDirection.Converging)
				    .endEvent()
				  .moveToLastGateway()
				  .condition("no", "${!approved}")
				  .userTask()
				    .name("Review Invoice")
				    .camundaAssignee("demo")
				  .connectTo("end")
				  .moveToLastGateway()
				  .condition("deafult", "${!default}")
				  .userTask()
				    .name("Review deafault Invoice")
				    .camundaAssignee("demo")
				  .connectTo("end")
				  .done();
		      Bpmn.writeModelToStream(System.out, modelInstance);
		}
	
	static void addInclusiveGateWay2() {

		/*BpmnModelInstance modelInstance = Bpmn
				.createExecutableProcess("invoice")
				.startEvent("start")
				.name("Start")
				.inclusiveGateway("start").endEvent().done();*/
          /*
		  BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
		  Definitions definitions = modelInstance.newInstance(Definitions.class);
		  definitions.setTargetNamespace("https://camunda.org/examples");
		  modelInstance.setDefinitions(definitions);
		  Process process = modelInstance.newInstance(Process.class);
		  definitions.addChildElement(process);*/
		  
		BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
	    Definitions definitions = modelInstance.newInstance(Definitions.class);
	    definitions.setTargetNamespace(BPMN20_NS);
	    definitions.getDomElement().registerNamespace("camunda", CAMUNDA_NS);
	    modelInstance.setDefinitions(definitions);
	    Process process = modelInstance.newInstance(Process.class);
	    definitions.addChildElement(process);

	    BpmnDiagram bpmnDiagram = modelInstance.newInstance(BpmnDiagram.class);

	    BpmnPlane bpmnPlane = modelInstance.newInstance(BpmnPlane.class);
	    bpmnPlane.setBpmnElement(process);

	    bpmnDiagram.addChildElement(bpmnPlane);
	    definitions.addChildElement(bpmnDiagram);
	    ////////////////////////
		  StartEvent startEvent = modelInstance.newInstance(StartEvent.class);
		  startEvent.setId("start");
		  process.addChildElement(startEvent);

		  ServiceTask serviceTask1 = modelInstance.newInstance(ServiceTask.class);
		  serviceTask1.setId("task1");
		  serviceTask1.setName("Service Task1");
		  
		  process.addChildElement(serviceTask1);
		  
		  ServiceTask serviceTask2 = modelInstance.newInstance(ServiceTask.class);
		  serviceTask1.setId("task2");
		  serviceTask1.setName("Service Task2");
		  process.addChildElement(serviceTask2);
		  
		  InclusiveGateway gatwayStart = modelInstance.newInstance(InclusiveGateway.class);
		  gatwayStart.setId("startGW");
		  gatwayStart.setName("startGW");
		  gatwayStart.setGatewayDirection(GatewayDirection.Diverging);
		  process.addChildElement(gatwayStart);
		  
		  InclusiveGateway gatwayEnd = modelInstance.newInstance(InclusiveGateway.class);
		  gatwayEnd.setId("endGW");
		  gatwayEnd.setName("endGW");
		  gatwayEnd.setGatewayDirection(GatewayDirection.Converging);
		  process.addChildElement(gatwayEnd);

		  EndEvent endEvent = modelInstance.newInstance(EndEvent.class);
		  endEvent.setId("end");
		  process.addChildElement(endEvent);

		  SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		  sequenceFlow.setId("flow1");
		  //sequenceFlow.setConditionExpression(conditionExpression);
		  process.addChildElement(sequenceFlow);
		  connect(sequenceFlow, startEvent, gatwayStart);
		  
		  sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		  sequenceFlow.setId("flow2");
		  process.addChildElement(sequenceFlow);
		  connect(sequenceFlow, gatwayStart, serviceTask1);
		  
		  sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		  sequenceFlow.setId("flow3");
		  process.addChildElement(sequenceFlow);
		  connect(sequenceFlow, gatwayStart, serviceTask2);
		  
		  sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		  sequenceFlow.setId("flow4");
		  process.addChildElement(sequenceFlow);
		  connect(sequenceFlow, serviceTask1, gatwayEnd);
		  
		  sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		  sequenceFlow.setId("flow5");
		  process.addChildElement(sequenceFlow);
		  connect(sequenceFlow, serviceTask2, gatwayEnd);
		  
		  sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
		  sequenceFlow.setId("flow6");
		  process.addChildElement(sequenceFlow);
		  connect(sequenceFlow, gatwayEnd,endEvent);

		  Bpmn.writeModelToStream(System.out, modelInstance);
	}
	
	static void BpmnShl() {
		/*
		BpmnModelInstance modelInstance = Bpmn.createExecutableProcess("invoice")
				.startEvent("start")
				.name("Start")
				.inclusiveGateway("InputparallelGateway")
				.condition("no", "${hello}")
				.serviceTask("Rule2")
				.inclusiveGateway("outputparallelGateway")
				.camundaExecutionListenerClass("jsonObject", "com.ey.wf.example.FirstCamundaProcess")
				.moveToNode("InputparallelGateway").condition("yes", "${hello}")
				.serviceTask("Rule3")
				.connectTo("outputparallelGateway")
				.sendTask("SendTask")
				.name("Send Outcome")
				.endEvent("end")
				.done();*/
		BpmnModelInstance modelInstance = Bpmn.createExecutableProcess("invoice")
				.startEvent("start")
				.name("Start")
				.inclusiveGateway("InputparallelGateway")
				.condition("flow2", "${hello.contains('flow2-immigration')}")
				.serviceTask("Rule2")
				.camundaClass("com.example.workflow.ServiceTaskDelegate2")
				.inclusiveGateway("outputparallelGateway")
				
				.moveToNode("InputparallelGateway").condition("flow3", "${hello.contains('flow3-immigration')}")
				.serviceTask("Rule3")
				.camundaClass("com.example.workflow.ServiceTaskDelegate3")
				.connectTo("outputparallelGateway")
				.moveToNode("InputparallelGateway").condition("flow1", "${hello.contains('flow1-immigration')}")
				.serviceTask("Rule1")
				.camundaClass("com.example.workflow.ServiceTaskDelegate1")
				.connectTo("outputparallelGateway")
				.endEvent("end")
				.camundaExecutionListenerClass("end", "com.example.workflow.InclusiveEndListener")
				.done();
		
		 Bpmn.writeModelToStream(System.out, modelInstance);
	}

	public static void main(String args[]) throws IOException {
		//addInclusiveGateWay();
		//addInclusiveGateWay2();  // breakage
		BpmnShl();
		//newCode();
	}
}
