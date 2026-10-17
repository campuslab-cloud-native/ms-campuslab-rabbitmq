package cl.campuslab.rabbitmq.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateExchangeRequest {

    @NotBlank(message = "Exchange name is required")
    private String name;

    @NotBlank(message = "Exchange type is required")
    @Pattern(regexp = "^(?i)(direct|topic|fanout)$", message = "Exchange type must be one of: direct, topic, fanout")
    private String type;

    private boolean durable = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }
}
