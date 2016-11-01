package com.aces.application.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.aces.application.models.WorkflowElement;

public interface WorkflowRepository extends CrudRepository<WorkflowElement, Integer> {
	
	@Query("select w from WorkflowElement w where w.parent is null")
	List<WorkflowElement> findRootElements();
}
