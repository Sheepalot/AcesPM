package com.aces.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aces.application.models.Memory;
import com.aces.application.repositories.MemoryRepository;

@Component("memoryService")
public class MemoryServiceImpl implements MemoryService{
	
	@Autowired
	MemoryRepository memoryRepository;

	@Override
	public Memory get() {
		return memoryRepository.findTop1ByIdIsNotNull();
	}

	@Override
	public void save(Memory toSave) {
		memoryRepository.save(toSave);
		
	}

	@Override
	public void delete(Memory toDelete) {
		memoryRepository.delete(toDelete);
	}
}
