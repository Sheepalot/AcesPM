package com.aces.application.services;

import com.aces.application.models.Memory;

public interface MemoryService {
	Memory get();
	
	void save(Memory toSave);
	void delete(Memory toDelete);
}
