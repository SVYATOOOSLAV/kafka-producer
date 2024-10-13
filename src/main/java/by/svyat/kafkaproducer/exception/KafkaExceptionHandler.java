package by.svyat.kafkaproducer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class KafkaExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleKafkaInternalException(KafkaInternalException e) {
        ErrorMessage message = new ErrorMessage(new Date(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }
}
