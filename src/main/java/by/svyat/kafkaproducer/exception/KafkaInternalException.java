package by.svyat.kafkaproducer.exception;

public class KafkaInternalException extends RuntimeException {
    public KafkaInternalException(String message) {
        super(message);
    }
}
