package com.fractallabs.assignment.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fractallabs.assignment.dao.CategoryRepository;
import com.fractallabs.assignment.dao.ResultsRepository;
import com.fractallabs.assignment.model.Category;
import com.fractallabs.assignment.model.Results;

@RunWith(SpringRunner.class)
public class CategoryServiceTest {

	@MockBean
	private ResultsRepository resultsRepo;

	@MockBean
	private CategoryRepository categoryRepo;
	
	@InjectMocks
	private CategoryService categoryService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void getCategorisedTransactionsTest() {
		
		Results result = new Results();
		List<Results> results = new ArrayList<>();
		results.add(result);
		when(resultsRepo.findByCategory("Travel")).thenReturn(results);
				
		assertEquals(1,categoryService.getCategorisedTransactions("Travel").size());	
		
	}
	
	
	@Test
	public void updateCategoryTest() {		
		Optional<Results> result = Optional.of(new Results());
		List<Results> results = new ArrayList<>();
		results.add(result.get());
	
		when(resultsRepo.findById("TestTransactionId")).thenReturn(result);
		
		Results updatedResult = new Results();
		updatedResult.setCategory("TestCategory");
		when(resultsRepo.save(any(Results.class))).thenReturn(updatedResult);
				
		assertEquals("TestCategory",categoryService.updateCategory("TestCategory","TestTransactionId").getCategory());	
	}
	
	@Test
	public void addCategoryTest() {
		Category category = new Category();		
		category.setCategoryName("Travel");
		when(categoryRepo.save(any(Category.class))).thenReturn(category);
		
		assertEquals("Travel",categoryService.addCategory(category).getCategoryName());
	}
}