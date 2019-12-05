package com.fractallabs.assignment.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@ApiModel(description="Details about a category")
public class Category {
	@ApiModelProperty(notes ="Category Id of a category")
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long categoryId;
	
	@ApiModelProperty(notes ="Category Name of a category")
	private String categoryName;
	
	@ApiModelProperty(notes ="Category Keywords of a category")
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="categoryId")
	private Set<Keyword> keywords;

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Set<Keyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<Keyword> keywords) {
		this.keywords = keywords;
	}

	

}
