package com.hotelsbook.services.com_hotelsbook_services.response;

public class ErrorResponse {
    
	private int code;
	private String description;
	
	public ErrorResponse(int code, String description) {
		super();
		this.code = code;
		this.description = description;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
