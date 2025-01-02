package by.svyat.kafkaproducer.producer;

import by.svyat.kafkacommon.event.ProductCreatedEvent;
import by.svyat.kafkaproducer.exception.KafkaInternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductProducer {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public CompletableFuture<SendResult<String, ProductCreatedEvent>> sendEventFuture(
            String messageId,
            ProductCreatedEvent event
    ) {
        ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                "product-created-event-topic",
                event.getProductId(),
                event
        );

        record.headers().add("messageId", messageId.getBytes());

        return kafkaTemplate.send(record);
    }

    public SendResult<String, ProductCreatedEvent> sendEventSync(
            String messageId,
            ProductCreatedEvent event
    ) {
        try {
            ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                    "product-created-event-topic",
                    event.getProductId(),
                    event
            );

            record.headers().add("messageId", messageId.getBytes());

            return kafkaTemplate.send(record).get();
        } catch (InterruptedException | ExecutionException  e) {
            log.error("Kafka producer exception: {}", e.getMessage());
            throw new KafkaInternalException(String.format("Kafka producer exception: %s", e.getMessage()));
        }
    }

}
