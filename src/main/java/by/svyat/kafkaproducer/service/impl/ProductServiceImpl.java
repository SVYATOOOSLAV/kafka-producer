package by.svyat.kafkaproducer.service.impl;

import by.svyat.kafkacommon.event.ProductCreatedEvent;
import by.svyat.kafkaproducer.controller.common.CreateProductDto;
import by.svyat.kafkaproducer.producer.ProductProducer;
import by.svyat.kafkaproducer.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductProducer productProducer;

    @Override
    public String createProduct(CreateProductDto product) {
        log.info("Started creating product");

        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId(productId)
                .title(product.getTitle())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();

        sendEventSync(event);

        sendEventFuture(event);

        log.info("Successfully created product with productId: {}", productId);

        return productId;
    }

    private void sendEventFuture(ProductCreatedEvent event){

        var future = productProducer.sendEventFuture(event);

        future.whenComplete((result, exception) -> {
            if(exception != null) {
                log.error("Something went wrong when sent event to kafka with message: {}", exception.getMessage());
            }
            else {
                log.info("Successfully sent async event with metadata: {}", result.getRecordMetadata());
            }
        });
    }

    private void sendEventSync(ProductCreatedEvent event){
        var result = productProducer.sendEventSync(event);
        log.info("Successfully sent event sync to partition: {}, for topic: {}, with offset: {}",
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().offset()
        );
    }
}
