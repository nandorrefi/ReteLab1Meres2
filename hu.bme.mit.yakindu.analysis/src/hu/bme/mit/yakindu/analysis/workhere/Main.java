package hu.bme.mit.yakindu.analysis.workhere;

import java.util.*;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.EList;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Entry;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void printTransitions(Vertex source, Vertex prev) {
		EList<Transition> transitions = source.getOutgoingTransitions();
		for(int i = 0; i < transitions.size(); i++) {
			Transition transition = transitions.get(i);
			Vertex target = transition.getTarget();
			
			System.out.println(source.getName() + " -> " + target.getName());
			
			if (target == prev || target == source) {
				continue;
			}
			
			printTransitions(target, source);
		}
	}
	
	public static void getDeadEnds(Vertex source, Vertex prev, Set<String> result) {	
		EList<Transition> transitions = source.getOutgoingTransitions();
		
		if(transitions.size() == 0) {
			result.add(source.getName());
			return;
		}
		
		for(int i = 0; i < transitions.size(); i++) {
			Transition transition = transitions.get(i);
			Vertex target = transition.getTarget();
			
			if (target == prev || target == source) {
				continue;
			}
			
			getDeadEnds(target, source, result);
		}
	}
	
	public static List<String> giveNames(Statechart s){
		TreeIterator<EObject> iterator = s.eAllContents();
		List<String> result = new ArrayList<String>();
		
		int counter = 0;
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof Vertex) {
				Vertex vertex = (Vertex) content;
				
				if(vertex.getName() == "") {
					StringBuilder strb = new StringBuilder("State");
					strb.append(counter);
					counter++;
					
					String newName = strb.toString();
					vertex.setName(newName);
					result.add(newName);
				}
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		
		giveNames(s);
		
		TreeIterator<EObject> iterator = s.eAllContents();
		Entry entry = null;
		
		System.out.println("States:");
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof Entry) {
				entry = (Entry) content;
			}
			else if(content instanceof State) {
				State state = (State) content;
				System.out.println(state.getName());
			}
		}
		
		System.out.println("\nTransitions:");
		printTransitions((Vertex)entry, (Vertex)entry);
		
		System.out.println("\nDead ends:");
		HashSet<String> deadEnds = new HashSet<String>();
		getDeadEnds((Vertex)entry, (Vertex)entry, deadEnds);
		
		for(int i = 0; i < deadEnds.size(); i++) {
			System.out.println(deadEnds.toArray()[0]);
		}
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
