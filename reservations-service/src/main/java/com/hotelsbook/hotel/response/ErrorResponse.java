package com.hotelsbook.hotel.response;

public class ErrorResponse {

    private int code;

    private String description;

    //Constructor
    public ErrorResponse(int code, String description) {
        this.code = code;
        this.description = description;
    }

    //Getter and Setter
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
