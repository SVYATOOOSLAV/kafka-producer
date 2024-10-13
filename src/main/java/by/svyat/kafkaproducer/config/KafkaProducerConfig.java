package by.svyat.kafkaproducer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.producer")
@Data
public class KafkaProducerConfig {
    private String bootstrapServers;
    private String keySerializer;
    private String valueSerializer;
    private String acks;
}
