package com.aces.application.services;

import java.util.List;

import com.aces.application.models.WorkflowElement;

public interface WorkflowService {
	
	List<WorkflowElement> findRootWorkflowElements();
	
	WorkflowElement findElementById(int id);
	
	void save(WorkflowElement toSave);
	void delete(WorkflowElement toDelete);
}
