package com.rasmoo.payment.aggregator.dto;

import java.math.BigDecimal;

public record BookReferenceDTO(
        String id,
        BigDecimal totalSold

) {
}
