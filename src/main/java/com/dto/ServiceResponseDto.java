package com.dto;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
public class ServiceResponseDto {

	public ServiceResponseDto(String message, List<String> description) {
		super();
		this.message = message;
		this.description = description;
	}



	@JsonProperty("messagecode")
	private String message;
	
	@JsonProperty("message")
	private List<String> description;


}
