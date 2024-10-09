package com.rasmoo.payment.aggregator.consumer;

import com.rasmoo.payment.aggregator.dto.BookReferenceDTO;
import com.rasmoo.payment.aggregator.dto.PaymentDTO;
import com.rasmoo.payment.aggregator.serdes.PaymentSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@EnableAsync
public class PaymentConsumer {

    @Autowired
    @Async
    public void buildPaymentProfileTopicPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, PaymentDTO> messageStream = streamsBuilder
                .stream("payment-topic", Consumed.with(Serdes.String(), PaymentSerdes.serdes()))
                .peek((key,value) -> System.out.println("Payment received: "+ value.id()))
                .filter((key, value) -> value.bookList().size() > 1);

        messageStream.print(Printed.toSysOut());
        messageStream.to("payment-profile-topic", Produced.with(Serdes.String(), PaymentSerdes.serdes()));
    }

    @Autowired
    @Async
    public void buildBookAggregatorTopicPipeline(StreamsBuilder streamsBuilder) {
        KTable<String, BigDecimal> messageStream = streamsBuilder
                .stream("payment-topic", Consumed.with(Serdes.String(), PaymentSerdes.serdes()))
                .filter((key, value) -> !value.bookList().isEmpty())
                .flatMap((key,value) ->
                        value.bookList().stream()
                                .map(book -> KeyValue.pair(book.id(),book.price())).toList())
                .groupBy((bookId, bookPrice) -> bookId, Grouped.with(Serdes.String(), PaymentSerdes.serdesBigDecimal()))
                .aggregate(
                        () -> BigDecimal.ZERO,
                        (bookId, bookPrice, aggregate) -> aggregate.add(bookPrice),
                        Materialized.with(Serdes.String(), PaymentSerdes.serdesBigDecimal())
                );

        messageStream.toStream().print(Printed.toSysOut());
        messageStream.toStream()
                .map((bookId, totalSold) -> {
                    BookReferenceDTO bookReferenceDTO = new BookReferenceDTO(
                            bookId,
                            totalSold
                    );
                    return KeyValue.pair(bookId, bookReferenceDTO);
                })
                .to("book-aggregator-topic", Produced.with(Serdes.String(),PaymentSerdes.serdesBookReference()));
    }

}
