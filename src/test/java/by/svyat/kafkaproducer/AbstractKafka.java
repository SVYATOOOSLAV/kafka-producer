package by.svyat.kafkaproducer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@DirtiesContext
// Если в рамках тестового метода был изменен контекст, то будет для нового теста предоствален обнолвеннный контекст
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Переиспользование объектов в рамках одного класса
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 3, // Количество партиций в топике
        count = 3, // Количество брокеров
        controlledShutdown = true, // Отсановка брокера только после завершения процессов
        kraft = true
)
@SpringBootTest(
        properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}" // Сервера Embedded Kafka
)
public class AbstractKafka {

    @Autowired
    public EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    public Environment environment;

    // Конфиг для консьюмера
    protected Map<String, Object> getConsumerProperties() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"));
        config.put(JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset"));

        return config;
    }

}
