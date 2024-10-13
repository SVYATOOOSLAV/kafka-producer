package by.svyat.kafkaproducer.service;

import by.svyat.kafkaproducer.controller.common.CreateProductDto;

public interface ProductService {
    String createProduct(CreateProductDto product);
}
