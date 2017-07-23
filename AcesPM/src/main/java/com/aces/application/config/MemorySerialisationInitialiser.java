package com.aces.application.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aces.application.models.Memory;
import com.aces.application.services.MemoryService;
import com.aces.application.utilities.AuditManagerHolder;
import com.aces.application.utilities.RunningAuditManager;

@Component
public class MemorySerialisationInitialiser {
	
	@Autowired
	private MemoryService memoryService;

	@PostConstruct
	public void init() {
		Memory m = memoryService.get();
		if (m == null) {
			m = new Memory();
			m.setManager(new RunningAuditManager());
			memoryService.save(m);
		}
		AuditManagerHolder.setManager(m.getManager());
	}
}
