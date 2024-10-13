package by.svyat.kafkaproducer.producer;

import by.svyat.kafkaproducer.exception.KafkaInternalException;
import by.svyat.kafkaproducer.producer.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductProducer {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public CompletableFuture<SendResult<String, ProductCreatedEvent>> sendEventFuture(ProductCreatedEvent event) {
        return kafkaTemplate.send(
                "product-created-event-topic",
                event.getProductId(),
                event
        );
    }

    public SendResult<String, ProductCreatedEvent> sendEventSync(ProductCreatedEvent event) {
        try {
            return kafkaTemplate.send(
                    "product-created-event-topic",
                    event.getProductId(),
                    event
            ).get();
        } catch (InterruptedException | ExecutionException  e) {
            log.error("Kafka producer exception: {}", e.getMessage());
            throw new KafkaInternalException(String.format("Kafka producer exception: %s", e.getMessage()));
        }
    }

}
