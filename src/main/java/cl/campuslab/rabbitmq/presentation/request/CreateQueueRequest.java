package cl.campuslab.rabbitmq.presentation.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public class CreateQueueRequest {

    @NotBlank(message = "Queue name is required")
    private String name;

    private boolean durable = true;

    private String deadLetterExchange;

    private String deadLetterRoutingKey;

    @AssertTrue(message = "Dead letter exchange and routing key must be provided together")
    public boolean isDeadLetterConfigurationValid() {
        boolean hasExchange = deadLetterExchange != null && !deadLetterExchange.isBlank();
        boolean hasRoutingKey = deadLetterRoutingKey != null && !deadLetterRoutingKey.isBlank();
        return !(hasExchange || hasRoutingKey) || (hasExchange && hasRoutingKey);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public String getDeadLetterExchange() {
        return deadLetterExchange;
    }

    public void setDeadLetterExchange(String deadLetterExchange) {
        this.deadLetterExchange = deadLetterExchange;
    }

    public String getDeadLetterRoutingKey() {
        return deadLetterRoutingKey;
    }

    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) {
        this.deadLetterRoutingKey = deadLetterRoutingKey;
    }
}
