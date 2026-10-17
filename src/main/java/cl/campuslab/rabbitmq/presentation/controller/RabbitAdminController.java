package cl.campuslab.rabbitmq.presentation.controller;

import cl.campuslab.rabbitmq.domain.service.RabbitAdminService;
import cl.campuslab.rabbitmq.presentation.request.CreateBindingRequest;
import cl.campuslab.rabbitmq.presentation.request.CreateExchangeRequest;
import cl.campuslab.rabbitmq.presentation.request.CreateQueueRequest;
import cl.campuslab.rabbitmq.presentation.request.DeleteBindingRequest;
import cl.campuslab.rabbitmq.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rabbit")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @PostMapping("/queues")
    public ResponseEntity<ApiResponse> createQueue(@Valid @RequestBody CreateQueueRequest request) {
        rabbitAdminService.createQueue(request.getName(), request.isDurable(), request.getDeadLetterExchange(), request.getDeadLetterRoutingKey());
        return ResponseEntity.ok(new ApiResponse("Queue created successfully"));
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<ApiResponse> deleteQueue(@PathVariable String name) {
        rabbitAdminService.deleteQueue(name);
        return ResponseEntity.ok(new ApiResponse("Queue deleted successfully"));
    }

    @PostMapping("/exchanges")
    public ResponseEntity<ApiResponse> createExchange(@Valid @RequestBody CreateExchangeRequest request) {
        rabbitAdminService.createExchange(request.getName(), request.getType(), request.isDurable());
        return ResponseEntity.ok(new ApiResponse("Exchange created successfully"));
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<ApiResponse> deleteExchange(@PathVariable String name) {
        rabbitAdminService.deleteExchange(name);
        return ResponseEntity.ok(new ApiResponse("Exchange deleted successfully"));
    }

    @PostMapping("/bindings")
    public ResponseEntity<ApiResponse> createBinding(@Valid @RequestBody CreateBindingRequest request) {
        rabbitAdminService.createBinding(request.getQueue(), request.getExchange(), request.getRoutingKey());
        return ResponseEntity.ok(new ApiResponse("Binding created successfully"));
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<ApiResponse> deleteBinding(@Valid @RequestBody DeleteBindingRequest request) {
        rabbitAdminService.deleteBinding(request.getQueue(), request.getExchange(), request.getRoutingKey());
        return ResponseEntity.ok(new ApiResponse("Binding deleted successfully"));
    }
}
