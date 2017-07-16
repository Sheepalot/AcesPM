package com.aces.application.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import com.aces.application.models.WorkflowElement;

public class RunningAuditManager {
	public static final HashMap<String, ArrayList<Question>> auditing = new HashMap<String, ArrayList<Question>>();
	
	public static void populate(String userName, WorkflowElement parentNode){
		ArrayList<Question> qs = new ArrayList<Question>();
		parentNode.children.forEach( e -> populate(e, qs));
		auditing.put(userName, qs);
	}
	
	public static void populate(WorkflowElement parentNode, ArrayList<Question> qs){
		Question q = new Question(parentNode.title);
		parentNode.responseSets.forEach(e -> {q.anwsers.put(e.title, 0);
		System.out.println(e.title);
		});
		qs.add(q);
		parentNode.children.forEach( e -> populate(e, qs));
	}
	
	public static boolean isRunningAudit(String username){
		return auditing.containsKey(username);
	}
	
	public static void clearFor(String username){
		auditing.remove(username);
	}
	
	public static void report(){
		auditing.entrySet().forEach(e -> {
			System.out.println(e.getKey());
			e.getValue().forEach(q -> {
				System.out.println("  "+q.title);
				q.anwsers.entrySet().forEach(a -> {
					System.out.println("  "+a.getKey()+ " "+a.getValue());
				});
			});
		});
	}
}
