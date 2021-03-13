package hu.bme.mit.yakindu.analysis.workhere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import org.yakindu.sct.model.sgraph.Statechart;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

import org.yakindu.sct.model.stext.stext.impl.VariableDefinitionImpl;
import org.yakindu.sct.model.stext.stext.impl.EventDefinitionImpl;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		
		TreeIterator<EObject> iterator = s.eAllContents();
		
		ArrayList<String> events = new ArrayList<String>();
		ArrayList<String> variables = new ArrayList<String>();
		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof EventDefinitionImpl) {
				EventDefinitionImpl event = (EventDefinitionImpl) content;
				events.add(event.getName());
			}
			else if(content instanceof VariableDefinitionImpl) {
				VariableDefinitionImpl variable = (VariableDefinitionImpl) content;
				variables.add(variable.getName());
			}
		}
		
		printImportLibraries();
		System.out.println();
		System.out.println("public class RunStatechart {");
		printMainFunction();
		System.out.println();
		printExecuteCommandFunction(events);
		System.out.println();
		printPrintVariablesFunction(variables);
		System.out.println("}");
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	
	public static void printMainFunction() {
		System.out.println(
		"public static void main(String[] args) throws IOException {\n" +
		"BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));\n" +	
		"ExampleStatemachine s = new ExampleStatemachine();\n" +
		"s.setTimer(new TimerService());\n" +
		"RuntimeService.getInstance().registerStatemachine(s, 200);\n" +
		"s.init();\n" +
		"s.enter();\n" +
		"s.runCycle();\n" +
		"while(true) {\n" +
		"String input = reader.readLine();\n" +
		"executeCommand(input, s);\n" +
		"print(s);\n" +
		"}\n" +
		"}"
		);
	}
	
	public static void printExecuteCommandFunction(List<String> events) {
		System.out.println(
				"public static void executeCommand(String input, IExampleStatemachine s) {"
				);
		for(int i = 0; i < events.size(); i++) {
			String capitalizedFirstLetter = events.get(i).substring(0, 1).toUpperCase();
			String capitalizedName = capitalizedFirstLetter + events.get(i).substring(1);
			
			if(i == 0) {
				System.out.print("if");
			}
			else {
				System.out.print("else if");
			}
			
			System.out.println(
				"(input.compareTo(\"" + events.get(i) + "\")==0) {\n" +
				"s.getSCInterface().raise" + capitalizedName + "();\n" +
				"}"
				);
		}
		
		System.out.println(
				"else if(input.compareTo(\"exit\")==0) {\n" +
				"System.exit(0);\n" +
				"}\n" + 	
				"s.runCycle();\n" +
				"}"
				);
	}
	
	public static void printImportLibraries() {
		System.out.println(
				"package hu.bme.mit.yakindu.analysis.workhere;" +
				"import java.io.IOException;\n" +
				"import hu.bme.mit.yakindu.analysis.RuntimeService;\n" +
				"import hu.bme.mit.yakindu.analysis.TimerService;\n" +
				"import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;\n" +
				"import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;\n" +
				"import java.io.BufferedReader;\n" +
				"import java.io.InputStreamReader;"
		);
	}
	
	public static void printPrintVariablesFunction(List<String> variables) {
		System.out.println("public static void print(IExampleStatemachine s){");
		for(int i = 0; i < variables.size(); i++) {
			String capitalizedFirstLetter = variables.get(i).substring(0, 1).toUpperCase();
			String capitalizedName = capitalizedFirstLetter + variables.get(i).substring(1);
			System.out.println(
					"System.out.println(\"" + variables.get(i) + 
					" = \" + s.getSCInterface().get" + capitalizedName + "());"
					);
		}
		System.out.println("}");
	}
}
