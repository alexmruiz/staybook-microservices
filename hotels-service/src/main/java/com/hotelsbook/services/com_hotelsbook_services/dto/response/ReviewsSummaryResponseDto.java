package com.hotelsbook.services.com_hotelsbook_services.dto.response;

import java.util.List;

public record ReviewsSummaryResponseDto(
    List<ReviewResponseDto> reviews,
    Double averageQualification
) {
    
}
