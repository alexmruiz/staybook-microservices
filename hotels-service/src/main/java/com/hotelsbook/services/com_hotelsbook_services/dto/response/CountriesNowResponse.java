package com.hotelsbook.services.com_hotelsbook_services.dto.response;

import java.util.List;

public record CountriesNowResponse(
    boolean error,
    String msg,
    List<String> data
) {
    
}
