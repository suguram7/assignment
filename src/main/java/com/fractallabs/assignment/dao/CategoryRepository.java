package com.fractallabs.assignment.dao;

import org.springframework.data.repository.CrudRepository;

import com.fractallabs.assignment.model.Category;

public interface CategoryRepository extends CrudRepository<Category, Long>{
	
}
