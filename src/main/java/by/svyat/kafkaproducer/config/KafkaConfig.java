package by.svyat.kafkaproducer.config;

import by.svyat.kafkacommon.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(value = KafkaProducerConfig.class)
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;
    private final KafkaProducerConfig kafkaProducerConfig;

    public Map<String, Object> producerConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerConfig.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.getValueSerializer());
        config.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfig.getAcks());
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, kafkaProperties.getDeliveryTimeoutMs());
        config.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProperties.getLingerMs());
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProperties.getRequestTimeoutMs());
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, kafkaProperties.getIsIdempotent());
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, kafkaProperties.getMaxInFlightRequestsPerConnection());

        return config;
    }

    @Bean
    public ProducerFactory<String, ProductCreatedEvent> producerFactoryForProduct(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactoryForProduct());
    }

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name("product-created-event-topic")
                .partitions(3)
                .replicas(3)
                .configs(
                        Map.of(
                                "min.insync.replicas", "1" // Count of sync replicas with leader min 1 (parameter ISR)
                        )
                )
                .build();
    }
}
