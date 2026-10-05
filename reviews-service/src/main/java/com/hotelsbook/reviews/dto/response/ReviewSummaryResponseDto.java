package com.hotelsbook.reviews.dto.response;

import java.util.List;

public record ReviewSummaryResponseDto(
    List<ReviewResponseDto> reviews,
    Double averageQualification
) {
    
}
