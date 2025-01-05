package by.svyat.kafkaproducer.integration;

import by.svyat.kafkacommon.event.ProductCreatedEvent;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IdempotentProducerTest {

    @Autowired
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @MockBean
    KafkaAdmin kafkaAdmin; // Для администрирования задач на кафка топике

    @Test
    void idempotentProducerConfigEnabledTest(){
        ProducerFactory<String, ProductCreatedEvent> producerFactory = kafkaTemplate.getProducerFactory();

        Map<String, Object> config = producerFactory.getConfigurationProperties();

        assertEquals(true, config.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        assertEquals("all", config.get(ProducerConfig.ACKS_CONFIG));

        if (config.get(ProducerConfig.RETRIES_CONFIG) != null) {
            assertTrue(
                    Integer.parseInt(config.get(ProducerConfig.RETRIES_CONFIG).toString()) > 0
            );
        }
    }
}
