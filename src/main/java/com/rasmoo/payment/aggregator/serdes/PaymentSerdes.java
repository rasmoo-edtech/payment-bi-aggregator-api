package com.rasmoo.payment.aggregator.serdes;

import com.rasmoo.payment.aggregator.dto.BookReferenceDTO;
import com.rasmoo.payment.aggregator.dto.PaymentDTO;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.math.BigDecimal;

public class PaymentSerdes extends Serdes.WrapperSerde<PaymentDTO>{

    public PaymentSerdes(Serializer<PaymentDTO> serializer, Deserializer<PaymentDTO> deserializer) {
        super(serializer, deserializer);
    }

    public static Serde<PaymentDTO> serdes() {
        JsonSerializer<PaymentDTO> serializer = new JsonSerializer<>();
        JsonDeserializer<PaymentDTO> deserializer = new JsonDeserializer<>(PaymentDTO.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<BigDecimal> serdesBigDecimal() {
        JsonSerializer<BigDecimal> serializer = new JsonSerializer<>();
        JsonDeserializer<BigDecimal> deserializer = new JsonDeserializer<>(BigDecimal.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<BookReferenceDTO> serdesBookReference() {
        JsonSerializer<BookReferenceDTO> serializer = new JsonSerializer<>();
        JsonDeserializer<BookReferenceDTO> deserializer = new JsonDeserializer<>(BookReferenceDTO.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
