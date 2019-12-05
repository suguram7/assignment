package com.fractallabs.assignment.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.fractallabs.assignment.model.Results;

public interface ResultsRepository extends CrudRepository<Results, String>{
	
	List<Results> findByCategory(String category);
}
