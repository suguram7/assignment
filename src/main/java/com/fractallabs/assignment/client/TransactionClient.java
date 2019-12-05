package com.fractallabs.assignment.client;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fractallabs.assignment.dao.CategoryRepository;
import com.fractallabs.assignment.dao.ResultsRepository;
import com.fractallabs.assignment.exceptions.ForbiddenException;
import com.fractallabs.assignment.exceptions.SystemException;
import com.fractallabs.assignment.model.AuthorizationResponse;
import com.fractallabs.assignment.model.BankTransactionResponse;
import com.fractallabs.assignment.model.Category;
import com.fractallabs.assignment.model.Results;

@Service
public class TransactionClient {

	private static final String X_PARTNER_ID = "X-Partner-Id";

	public static final String X_API_KEY = "X-Api-Key";

	private static final Logger logger = LoggerFactory.getLogger(TransactionClient.class);

	@Value("${fractal.tokenUrl}")
	private String tokenUrl;

	@Value("${fractal.transactionUrl}")
	private String transactionUrl;

	@Value("${fractal.apiKey}")
	private String apiKey;

	@Value("${fractal.partnerId}")
	private String partnerId;

	@Autowired
	private ResultsRepository resultsRepo;

	@Autowired
	private CategoryRepository categoryRepo;

	
	@Autowired
	private RestTemplate template; 
	
	@Bean
	public RestTemplate getTemplate() {
		return new RestTemplate();
	}

	/**
	 * Function to retrieve authorization token from the client.
	 * 
	 * @return string - Authorization token.
	 */
	public String getAuthToken() {
		HttpHeaders headers = new HttpHeaders();
		String token = "";
		headers.set(X_API_KEY, apiKey);
		headers.set(X_PARTNER_ID, partnerId);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

		try {
			ResponseEntity<AuthorizationResponse> response = template.exchange(tokenUrl, HttpMethod.POST, entity,
					AuthorizationResponse.class);
			AuthorizationResponse authResponse = response.getBody();
			token = authResponse.getAccess_token();
		} catch (HttpClientErrorException ex) {
			if (ex.getRawStatusCode() == 403) {
				logger.error("Access denied.", ex);
				throw new ForbiddenException("Access denied");
			} else 
				throw new SystemException(ex.getMessage());
		} catch (Exception e) {
			logger.error("System error.", e);
			throw new SystemException("An error occurred");
		}
		return token;
	}

	/**
	 * Function to retrieve the bank transactions for the given bankid and
	 * accountID.
	 * 
	 * @param bankId
	 *            - Bank ID
	 * @param accountId
	 *            - Account ID
	 * @return 
	 */
	public List<Results> getBankTransactions(String bankId, String accountId) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(X_API_KEY, apiKey);
		headers.set(X_PARTNER_ID, partnerId);
		headers.set("Authorization", "Bearer " + getAuthToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

		try {
			ResponseEntity<BankTransactionResponse> response = template.exchange(transactionUrl, HttpMethod.GET,
					entity, BankTransactionResponse.class, bankId, accountId);
			BankTransactionResponse bankTransactionResponse = response.getBody();

			List<Category> categories = (List<Category>) categoryRepo.findAll();

			bankTransactionResponse.getResults().forEach(resp -> {
				categories.forEach(category -> {
					List<String> keywords = category.getKeywords().stream().map(key -> key.getTransactionKeyword())
							.collect(Collectors.toList());
					keywords.forEach(keyword -> {
						if (resp.getDescription().contains(keyword)) {
							logger.info("Match found for keyword " + keyword + " with description : "
									+ resp.getDescription());
							resp.setCategory(category.getCategoryName());
						}
					});
				});
			});

			return (List<Results>) resultsRepo.saveAll(bankTransactionResponse.getResults());

		} catch (HttpClientErrorException ex) {
			if (ex.getRawStatusCode() == 403) {
				logger.error("Access denied.", ex);
				throw new ForbiddenException("Access denied");
			} else 
				throw new SystemException(ex.getMessage());
		} catch (Exception e) {
			logger.error("System error.", e);
			throw new SystemException("An error occurred when fetching transactions");
		}

	}

}