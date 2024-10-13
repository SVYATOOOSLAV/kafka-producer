package by.svyat.kafkaproducer.exception;

import java.util.Date;

public record ErrorMessage(
        Date timestamp,
        String errorMessage
) { }
