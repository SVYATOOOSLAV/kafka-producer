package by.svyat.kafkaproducer.controller;

import by.svyat.kafkaproducer.controller.common.CreateProductDto;
import by.svyat.kafkaproducer.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<String> createProduct(
            @RequestHeader("message_id") String messageId,
            @RequestBody CreateProductDto product
    ){
        String productId = productService.createProduct(messageId, product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}
