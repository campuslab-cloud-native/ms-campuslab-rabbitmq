package cl.campuslab.rabbitmq.exception;

import cl.campuslab.rabbitmq.presentation.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsConstraintViolationsToBadRequest() {
        ResponseEntity<ApiResponse> response =
                handler.handleConstraintViolationException(new ConstraintViolationException("invalid", null));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("invalid", response.getBody().getMessage());
    }

    @Test
    void mapsIllegalArgumentsToBadRequest() {
        ResponseEntity<ApiResponse> response =
                handler.handleIllegalArgumentException(new IllegalArgumentException("bad input"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("bad input", response.getBody().getMessage());
    }

    @Test
    void mapsAmqpFailuresToBadGateway() {
        ResponseEntity<ApiResponse> response =
                handler.handleAmqpException(new AmqpException("broker unavailable"));

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("RabbitMQ operation failed: broker unavailable", response.getBody().getMessage());
    }

    @Test
    void mapsUnexpectedFailuresToInternalServerError() {
        ResponseEntity<ApiResponse> response =
                handler.handleGenericException(new Exception("unexpected"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: unexpected", response.getBody().getMessage());
    }
}
