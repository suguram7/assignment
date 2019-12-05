package com.fractallabs.assignment.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Keyword {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String transactionKeyword;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public String getTransactionKeyword() {
		return transactionKeyword;
	}
	public void setTransactionKeyword(String transactionKeyword) {
		this.transactionKeyword = transactionKeyword;
	}
}
