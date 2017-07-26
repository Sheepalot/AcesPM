package com.aces.application.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.aces.application.models.WorkflowElement;

public class RunningAuditManager implements Serializable{

	//Ultimately this will be replaced by some user (or group of users) identifier
	public static final String CURRENT_AUDIT = "CURRENT_AUDIT";
	
	public HashMap<String, ArrayList<Question>> auditing = new HashMap<String, ArrayList<Question>>();
	
	public void populate(WorkflowElement parentNode){
		ArrayList<Question> qs = new ArrayList<Question>();
		populate(parentNode, qs);
		auditing.put(CURRENT_AUDIT, qs);
		parentNode.getResponseSets().forEach(r -> {
			ArrayList<Question> dupes = new ArrayList<Question>();
			populate(parentNode, dupes);
			auditing.put(CURRENT_AUDIT+" "+r.getTitle(), dupes);
		});
		auditing.put(CURRENT_AUDIT, new ArrayList<Question>(qs));
	}
	
	public void populate(WorkflowElement parentNode, ArrayList<Question> qs){
		Question q = new Question(parentNode.title);
		parentNode.responseSets.forEach(e -> {q.anwsers.add(e.title);});
		qs.add(q);
		parentNode.children.forEach( e -> populate(e, qs));
	}
	
	public boolean isRunningAudit(){
		return auditing.containsKey(CURRENT_AUDIT);
	}
	
	public void clear(){
		ArrayList<String> keysToRemove = new ArrayList<String>();
		auditing.keySet().forEach(k ->{
			if(k.startsWith(CURRENT_AUDIT)){
				keysToRemove.add(k);
			}
		});
		keysToRemove.forEach(k -> {
			auditing.remove(k);
		});
	}
	
	public ArrayList<Question> getQuestions(){
		return auditing.get(CURRENT_AUDIT);
	}
	
	public void prepareResults(LinkedHashMap<String, List<Question>> results){
		auditing.entrySet().forEach(e -> {
			if(e.getKey().startsWith(CURRENT_AUDIT) && e.getKey().length() > CURRENT_AUDIT.length()){
				results.put(e.getKey().split(" ")[1], e.getValue().subList(1, e.getValue().size()));
			}
		});
	}
	
	public void submitAnswers(String[] answers){
		ArrayList<Question> questions = auditing.get(CURRENT_AUDIT+ " "+answers[0]);
		
		//No school like old school for loop, index is important to us here
		for(int i=1; i<answers.length; i++){
			Question current = questions.get(i);
			Integer existingCount = current.results.get(answers[i]);
			if(existingCount==null){
				existingCount = 0;
			}
			current.results.put(answers[i], existingCount+1);
		}
	}
}
