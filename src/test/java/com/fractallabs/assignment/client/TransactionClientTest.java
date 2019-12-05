package com.fractallabs.assignment.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fractallabs.assignment.dao.CategoryRepository;
import com.fractallabs.assignment.dao.ResultsRepository;
import com.fractallabs.assignment.exceptions.ForbiddenException;
import com.fractallabs.assignment.exceptions.SystemException;
import com.fractallabs.assignment.model.AuthorizationResponse;
import com.fractallabs.assignment.model.BankTransactionResponse;
import com.fractallabs.assignment.model.Category;
import com.fractallabs.assignment.model.Keyword;
import com.fractallabs.assignment.model.Results;

@RunWith(SpringRunner.class)
public class TransactionClientTest {

	@InjectMocks
	private TransactionClient transactionClient;
	
	@Mock
	private RestTemplate template;
	
	@Mock
	private ResultsRepository resultsRepo;

	@Mock
	private CategoryRepository categoryRepo;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(transactionClient, "tokenUrl", "https://testUrl");
		ReflectionTestUtils.setField(transactionClient, "transactionUrl", "https://testTransactionUrl");
		ReflectionTestUtils.setField(transactionClient, "apiKey", "testApiKey");
		ReflectionTestUtils.setField(transactionClient, "partnerId", "testPartnerId");
	}
	
	@Test
	public void getBankTransactionsTest() {

		BankTransactionResponse bankTransactionResponse = new BankTransactionResponse();
		Results result = new Results();
		result.setAccountId("test");
		result.setDescription("testKeyword");
		List<Results> resultsList  = new ArrayList<>();
		resultsList.add(result);
		bankTransactionResponse.setResults(resultsList);
		
		ResponseEntity<BankTransactionResponse> response = new ResponseEntity<BankTransactionResponse>(bankTransactionResponse, HttpStatus.OK);
		
		when(template.exchange(anyString(), any(HttpMethod.class)
				, any(HttpEntity.class), ArgumentMatchers.<Class<BankTransactionResponse>> any()
				, anyString(), anyString())).thenReturn(response);
		
		Category category = new Category();
		Keyword keyword = new Keyword();
		keyword.setTransactionKeyword("testKeyword");
		keyword.setId(1L);
		Set<Keyword> keywordSet = new HashSet<>();
		keywordSet.add(keyword);
		category.setKeywords(keywordSet);
		category.setCategoryId(1L);
		category.setCategoryName("testCategoryName");
		List<Category> categoryList = new ArrayList<>();
		categoryList.add(category);
		
		when(categoryRepo.findAll()).thenReturn(categoryList);
		
		when(resultsRepo.saveAll(bankTransactionResponse.getResults())).thenReturn(resultsList);
		
		AuthorizationResponse authorizationResponse = new AuthorizationResponse();
		authorizationResponse.setAccess_token("test_token");
		
		ResponseEntity<AuthorizationResponse> authResponse = new ResponseEntity<AuthorizationResponse>(authorizationResponse,HttpStatus.OK);
		when(template.exchange(anyString(), any(HttpMethod.class)
				, any(HttpEntity.class), ArgumentMatchers.<Class<AuthorizationResponse>> any())).thenReturn(authResponse);
		
		assertEquals(1, transactionClient.getBankTransactions("bankId", "accountId").size());
		
	}
	
	@Test(expected = ForbiddenException.class)
	public void getBankTransactionsForbiddenTest() {

		BankTransactionResponse bankTransactionResponse = new BankTransactionResponse();
		Results result = new Results();
		result.setAccountId("test");
		result.setDescription("testKeyword");
		List<Results> resultsList  = new ArrayList<>();
		resultsList.add(result);
		bankTransactionResponse.setResults(resultsList);
		
		when(template.exchange(anyString(), any(HttpMethod.class)
				, any(HttpEntity.class), ArgumentMatchers.<Class<BankTransactionResponse>> any()
				, anyString(), anyString())).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));
		
		AuthorizationResponse authorizationResponse = new AuthorizationResponse();
		authorizationResponse.setAccess_token("test_token");
		
		ResponseEntity<AuthorizationResponse> authResponse = new ResponseEntity<AuthorizationResponse>(authorizationResponse,HttpStatus.OK);
		when(template.exchange(anyString(), any(HttpMethod.class)
				, any(HttpEntity.class), ArgumentMatchers.<Class<AuthorizationResponse>> any())).thenReturn(authResponse);
		
		transactionClient.getBankTransactions("bankId", "accountId");
		
	}
	
	@Test(expected = SystemException.class)
	public void getBankTransactionsSystemExceptionTest() {

		BankTransactionResponse bankTransactionResponse = new BankTransactionResponse();
		Results result = new Results();
		result.setAccountId("test");
		result.setDescription("testKeyword");
		List<Results> resultsList  = new ArrayList<>();
		resultsList.add(result);
		bankTransactionResponse.setResults(resultsList);
		
		when(template.exchange(anyString(), any(HttpMethod.class)
				, any(HttpEntity.class), ArgumentMatchers.<Class<BankTransactionResponse>> any()
				, anyString(), anyString())).thenThrow(new RuntimeException("An error occurred"));
		
		AuthorizationResponse authorizationResponse = new AuthorizationResponse();
		authorizationResponse.setAccess_token("test_token");
		
		ResponseEntity<AuthorizationResponse> authResponse = new ResponseEntity<AuthorizationResponse>(authorizationResponse,HttpStatus.OK);
		when(template.exchange(anyString(), any(HttpMethod.class)
				, any(HttpEntity.class), ArgumentMatchers.<Class<AuthorizationResponse>> any())).thenReturn(authResponse);
		
		transactionClient.getBankTransactions("bankId", "accountId");
		
	}
}

