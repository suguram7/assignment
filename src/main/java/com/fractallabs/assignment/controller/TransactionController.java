package com.fractallabs.assignment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fractallabs.assignment.client.TransactionClient;
import com.fractallabs.assignment.model.Category;
import com.fractallabs.assignment.model.Results;
import com.fractallabs.assignment.model.UpdateTransactionRequest;
import com.fractallabs.assignment.service.CategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/")
@Api(value = "Transaction Category Management")
public class TransactionController {

	@Autowired
	private TransactionClient transactionClient;

	@Autowired
	private CategoryService categoryService;

	/**
	 * Operation to Retrieve bank transactions and categorise based on the
	 * appropriate category.
	 * 
	 * @param bankId
	 *            - Bank ID
	 * @param accountId
	 *            - Account ID
	 */
	@ApiOperation(value = "Retrieve bank transactions and categorise based on the appropriate category")
	@GetMapping("transactions/{bankId}/{accountId}")
	public void transactions(@PathVariable("bankId") String bankId, @PathVariable("accountId") String accountId) {
		transactionClient.getBankTransactions(bankId, accountId);
	}

	/**
	 * 
	 * Operation to Retrieve transactions for a specified category.
	 * 
	 * @param category
	 *            - Category based on which transactions are retrieved
	 * @return List of Transactions
	 */
	@ApiOperation(value = "Retrieve transactions for a specified category", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved transactions"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden") })
	@GetMapping("category/{category}")
	public ResponseEntity<List<Results>> getCategorisedTransactions(
			@ApiParam(value = "Category based on which transactions are retrieved") @PathVariable("category") String category) {
		List<Results> results = categoryService.getCategorisedTransactions(category);
		return new ResponseEntity<>(results, HttpStatus.OK);
	}

	/**
	 * Operation to Update category of an existing transaction.
	 * 
	 * @param transactionId
	 *            - TransactionId to update the category.
	 * @param updateTransactionRequest
	 *            - UpdateTransactionRequest object Holds category to be updated as.
	 * @return Updated Transaction object.
	 */
	@ApiOperation(value = "Update category of an existing transaction")
	@PutMapping("category/{id}")
	public ResponseEntity<Results> updateCategory(
			@ApiParam(value = "TransactionId to update the category") @PathVariable("id") String transactionId,
			@ApiParam(value = "Holds category to be updated as") @RequestBody UpdateTransactionRequest updateTransactionRequest) {
		Results result = categoryService.updateCategory(updateTransactionRequest.getCategory(), transactionId);
		return new ResponseEntity<Results>(result, HttpStatus.OK);
	}

	/**
	 * Operation to Add a new category.
	 * 
	 * @param category
	 *            - Category object to be created.
	 *  @return - Saved category
	 * 
	 */
	@ApiOperation(value = "Add a new category")
	@PostMapping("category")
	public ResponseEntity<Category> addCategory(
			@ApiParam(value = "Category object to be created", required = true) @RequestBody Category category) {
		Category savedCategory = categoryService.addCategory(category);
		return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
	}
}