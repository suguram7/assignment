package com.fractallabs.assignment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fractallabs.assignment.dao.CategoryRepository;
import com.fractallabs.assignment.dao.ResultsRepository;
import com.fractallabs.assignment.exceptions.TransactionNotFoundException;
import com.fractallabs.assignment.model.Category;
import com.fractallabs.assignment.model.Results;

@Service
public class CategoryService {

	@Autowired
	private ResultsRepository resultsRepo;

	@Autowired
	private CategoryRepository categoryRepo;

	/**
	 * Function to retrieve categorised transactions based on a specific category.
	 * 
	 * @param category
	 *            - category name.
	 * @return List<Results> - Collection of transactions
	 */
	public List<Results> getCategorisedTransactions(String category) {
		return resultsRepo.findByCategory(category);
	}

	/**
	 * Function to update the category of an existing transaction.
	 * 
	 * @param category
	 *            - category name to be updated.
	 * @param transactionId
	 *            - Transaction Id of the transaction.
	 * @return Results
	 */
	public Results updateCategory(String category, String transactionId) {

		Optional<Results> transaction = resultsRepo.findById(transactionId);

		Results updatedResult = transaction.map(t -> {
			t.setCategory(category);
			return t;
		}).orElseThrow(() -> new TransactionNotFoundException("Transaction not found!"));

		return resultsRepo.save(updatedResult);
	}

	/**
	 * Function to add a new category to the database.
	 * 
	 * @param category
	 *            - Object holding the category details.
	 * @return 
	 */
	public Category addCategory(Category category) {
		return categoryRepo.save(category);
	}
}