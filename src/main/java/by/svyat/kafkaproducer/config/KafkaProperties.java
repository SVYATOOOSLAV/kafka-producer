package by.svyat.kafkaproducer.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Data
class KafkaProperties {
    @Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
    private String deliveryTimeoutMs;

    @Value("${spring.kafka.producer.properties.linger.ms}")
    private String lingerMs;

    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    private String requestTimeoutMs;

    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    private Boolean isIdempotent;

    @Value("${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    private Integer maxInFlightRequestsPerConnection;
}