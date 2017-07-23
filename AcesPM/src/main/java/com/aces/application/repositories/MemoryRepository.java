package com.aces.application.repositories;

import org.springframework.data.repository.CrudRepository;

import com.aces.application.models.Memory;

public interface MemoryRepository extends CrudRepository<Memory, Integer> {
	
	 public Memory findTop1ByIdIsNotNull();
}
