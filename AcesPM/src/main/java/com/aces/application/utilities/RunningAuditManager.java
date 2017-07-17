package com.aces.application.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import com.aces.application.models.WorkflowElement;

public class RunningAuditManager {
	public static final HashMap<String, ArrayList<Question>> auditing = new HashMap<String, ArrayList<Question>>();
	
	public static void populate(String userName, WorkflowElement parentNode){
		ArrayList<Question> qs = new ArrayList<Question>();
		populate(parentNode, qs);
		auditing.put(userName, qs);
		parentNode.getResponseSets().forEach(r -> {
			ArrayList<Question> dupes = new ArrayList<Question>();
			populate(parentNode, dupes);
			auditing.put(userName+" "+r.getTitle(), dupes);
		});
		auditing.put(userName, new ArrayList<Question>(qs));
	}
	
	public static void populate(WorkflowElement parentNode, ArrayList<Question> qs){
		Question q = new Question(parentNode.title);
		parentNode.responseSets.forEach(e -> {q.anwsers.add(e.title);});
		qs.add(q);
		parentNode.children.forEach( e -> populate(e, qs));
	}
	
	public static boolean isRunningAudit(String username){
		return auditing.containsKey(username);
	}
	
	public static void clearFor(String username){
		ArrayList<String> keysToRemove = new ArrayList<String>();
		auditing.keySet().forEach(k ->{
			if(k.startsWith(username)){
				keysToRemove.add(k);
			}
		});
		keysToRemove.forEach(k -> {
			auditing.remove(k);
		});
	}
	
	public static void report(){
		auditing.entrySet().forEach(e -> {
			System.out.println(e.getKey());
			e.getValue().forEach(q -> {
				System.out.println("  "+q.title);
				q.results.entrySet().forEach(a -> {
					System.out.println("  "+a.getKey()+ " "+a.getValue());
				});
			});
		});
	}
}
