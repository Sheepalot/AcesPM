package com.aces.application.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aces.application.models.WorkflowElement;
import com.aces.application.repositories.WorkflowRepository;

@Component("workflowService")
public class WorkflowServiceImpl implements WorkflowService{
	
	@Autowired
	private WorkflowRepository workflowRepository;
	

	@Override
	public List<WorkflowElement> findRootWorkflowElements() {
		return (List<WorkflowElement>) workflowRepository.findRootElements();
	}


	@Override
	public WorkflowElement findElementById(int id) {
		return workflowRepository.findOne(id);
	}


	@Override
	public void save(WorkflowElement toSave) {
		workflowRepository.save(toSave);
		
	}
	
	@Override
	public void delete(WorkflowElement toDelete) {
		workflowRepository.delete(toDelete);
		
	}
}
