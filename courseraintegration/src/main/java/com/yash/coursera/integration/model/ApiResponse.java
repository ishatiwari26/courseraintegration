package com.yash.coursera.integration.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ApiResponse {

	private List<Element> elements;

	@JsonIgnore
	private Object paging;

	@JsonIgnore
	private String linked;

}
