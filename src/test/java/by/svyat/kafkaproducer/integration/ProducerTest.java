package by.svyat.kafkaproducer.integration;

import by.svyat.kafkacommon.event.ProductCreatedEvent;
import by.svyat.kafkaproducer.AbstractKafka;
import by.svyat.kafkaproducer.controller.common.CreateProductDto;
import by.svyat.kafkaproducer.service.ProductService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ProducerTest extends AbstractKafka {

    @Autowired
    private ProductService productService;

    private KafkaMessageListenerContainer<String, ProductCreatedEvent> container;

    private BlockingQueue<ConsumerRecord<String, ProductCreatedEvent>> records;

    @BeforeAll
    void setUp() {
        ConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties("product-created-event-topic"); // Какой топик / топики читать

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties); // Этот объект будет получать сообщения
        records = new LinkedBlockingQueue<>(); // хранилище сообщений
        container.setupMessageListener((MessageListener<String, ProductCreatedEvent>) records::add); // как только пришло сообщение добавляем его в очередь
        container.start();

        // Ожиадем пока все три партиции будут заасайнены на консьюмеров
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }

    @Test
    void sendTwoKafkaMessageWithCorrectlyDataTest() throws InterruptedException {
        String messageId = "9876";
        String productName = "TEST_PRODUCT";
        BigDecimal price = new BigDecimal("100.00");
        Integer quantity = 10;
        CreateProductDto productDto = new CreateProductDto(productName, price, quantity);

        productService.createProduct(messageId, productDto);

        for (int i = 0; i < 2; i++) {
            ConsumerRecord<String, ProductCreatedEvent> record = records.poll(3000, TimeUnit.MILLISECONDS); //Ждет сообщений в течение 3 секунд

            assertNotNull(record);
            assertNotNull(record.key());

            ProductCreatedEvent event = record.value();
            assertEquals(productDto.getTitle(), event.getTitle());
            assertEquals(productDto.getPrice(), event.getPrice());
            assertEquals(productDto.getQuantity(), event.getQuantity());
        }
    }

    @Test
    void highVolumeMessageTest() throws InterruptedException {
        int messageCount = 100;
        String productName = "HIGH_VOLUME_TEST_PRODUCT";
        BigDecimal price = new BigDecimal("10.00");
        Integer quantity = 1;

        for (int i = 0; i < messageCount; i++) {
            String messageId = "msg-" + i;
            CreateProductDto productDto = new CreateProductDto(productName + i, price, quantity);
            productService.createProduct(messageId, productDto);
        }

        // send sync and async message
        for (int i = 0; i < messageCount * 2; i++) {
            ConsumerRecord<String, ProductCreatedEvent> record = records.poll(5000, TimeUnit.MILLISECONDS);

            assertNotNull(record);
            ProductCreatedEvent event = record.value();
            assertTrue(event.getTitle().startsWith(productName));
        }
    }
}