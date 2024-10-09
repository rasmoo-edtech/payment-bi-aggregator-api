package com.rasmoo.payment.aggregator.dto;



import java.math.BigDecimal;
import java.util.List;

public record PaymentDTO(

        String id,

        UserInfoDTO userInfo,

        List<BookDTO> bookList

) {
    public record UserInfoDTO(

            String firstName,
            String lastName,
            String cpf
    ) {

    }

    public record BookDTO(
            String id,
            String title,
            BigDecimal price


    ) {
    }
}
