package com.fractallabs.assignment.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fractallabs.assignment.client.TransactionClient;
import com.fractallabs.assignment.model.Category;
import com.fractallabs.assignment.model.Results;
import com.fractallabs.assignment.model.UpdateTransactionRequest;
import com.fractallabs.assignment.service.CategoryService;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

	@MockBean
	private CategoryService categoryService;
	
	@MockBean
	private TransactionClient transactionClient;

	@InjectMocks
	private TransactionController transactionController;

	@Autowired
	private MockMvc mvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getCategorisedTransactionsTest() throws Exception {

		Results result = new Results();
		List<Results> results = new ArrayList<>();
		results.add(result);
		when(categoryService.getCategorisedTransactions("Travel")).thenReturn(results);
		
		transactionController.getCategorisedTransactions("Travel");

		mvc.perform(MockMvcRequestBuilders.get("/category/Travel")).andExpect(status().isOk());

	}

	@Test
    public void addCategoryTest() throws Exception {
		Category category = new Category();
		when(categoryService.addCategory(category)).thenReturn(category);
		
		transactionController.addCategory(category);
		
        mvc.perform(MockMvcRequestBuilders.post("/category")
        	.content(asJsonString(category))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }
	
	@Test
    public void updateCategoryTest() throws Exception {
		UpdateTransactionRequest updateTransactionRequest = new UpdateTransactionRequest();
		updateTransactionRequest.setCategory("TestCategory");
		Results results = new Results();
		when(categoryService.updateCategory(updateTransactionRequest.getCategory(), "TestTransactionId")).thenReturn(results);
		
		transactionController.updateCategory("TestTransactionId", updateTransactionRequest);
		
        mvc.perform(MockMvcRequestBuilders.put("/category/TestTransactionId")
        	.content(asJsonString(updateTransactionRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
	
	private static String asJsonString(final Object obj) {
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
