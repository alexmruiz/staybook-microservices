package com.hotelsbook.reviews.exception;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(String message){
        super(message);
    }

}
