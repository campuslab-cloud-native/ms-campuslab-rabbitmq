package cl.campuslab.rabbitmq.presentation.request;

import jakarta.validation.constraints.NotBlank;

public class CreateBindingRequest {

    @NotBlank(message = "Queue name is required")
    private String queue;

    @NotBlank(message = "Exchange name is required")
    private String exchange;

    @NotBlank(message = "Routing key is required")
    private String routingKey;

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
