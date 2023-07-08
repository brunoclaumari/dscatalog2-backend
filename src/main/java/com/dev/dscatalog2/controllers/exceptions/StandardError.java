package com.dev.dscatalog2.controllers.exceptions;

import java.io.Serializable;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

public class StandardError implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private Instant timeStamp;
	
	@Getter
	@Setter
	private Integer status;
	
	@Getter
	@Setter
	private String error;
	
	@Getter
	@Setter
	private String message;
	
	@Getter
	@Setter
	private String path;
}
