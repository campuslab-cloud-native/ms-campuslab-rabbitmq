package cl.campuslab.rabbitmq.data.service;

import cl.campuslab.rabbitmq.config.MessagingNames;
import cl.campuslab.rabbitmq.domain.service.RabbitAdminService;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class RabbitAdminServiceImpl implements RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminServiceImpl(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void createQueue(String name, boolean durable, String deadLetterExchange, String deadLetterRoutingKey) {
        String validatedName = validateName(name, "Queue");

        QueueBuilder queueBuilder = durable ? QueueBuilder.durable(validatedName) : QueueBuilder.nonDurable(validatedName);

        if (deadLetterExchange != null && !deadLetterExchange.isBlank()) {
            String validatedExchange = validateName(deadLetterExchange, "Dead letter exchange");
            String validatedRoutingKey = validateName(deadLetterRoutingKey, "Dead letter routing key");
            queueBuilder = queueBuilder.withArgument("x-dead-letter-exchange", validatedExchange)
                    .withArgument("x-dead-letter-routing-key", validatedRoutingKey);
        } else if (deadLetterRoutingKey != null && !deadLetterRoutingKey.isBlank()) {
            throw new IllegalArgumentException("Dead letter exchange is required when dead letter routing key is configured");
        }

        rabbitAdmin.declareQueue(queueBuilder.build());
    }

    @Override
    public void deleteQueue(String name) {
        rabbitAdmin.deleteQueue(validateName(name, "Queue"));
    }

    @Override
    public void createExchange(String name, String type, boolean durable) {
        String validatedName = validateName(name, "Exchange");
        String normalizedType = validateExchangeType(type);

        Exchange exchange = switch (normalizedType) {
            case MessagingNames.EXCHANGE_TYPE_DIRECT -> new DirectExchange(validatedName, durable, false);
            case MessagingNames.EXCHANGE_TYPE_TOPIC -> new TopicExchange(validatedName, durable, false);
            case MessagingNames.EXCHANGE_TYPE_FANOUT -> new FanoutExchange(validatedName, durable, false);
            default -> throw new IllegalArgumentException("Unsupported exchange type: " + type);
        };

        rabbitAdmin.declareExchange(exchange);
    }

    @Override
    public void deleteExchange(String name) {
        rabbitAdmin.deleteExchange(validateName(name, "Exchange"));
    }

    @Override
    public void createBinding(String queue, String exchange, String routingKey) {
        String validatedQueue = validateName(queue, "Queue");
        String validatedExchange = validateName(exchange, "Exchange");
        String validatedRoutingKey = validateName(routingKey, "Routing key");

        Queue queueInstance = QueueBuilder.durable(validatedQueue).build();
        DirectExchange exchangeInstance = new DirectExchange(validatedExchange, true, false);

        Binding binding = BindingBuilder.bind(queueInstance)
                .to(exchangeInstance)
                .with(validatedRoutingKey);

        rabbitAdmin.declareBinding(binding);
    }

    @Override
    public void deleteBinding(String queue, String exchange, String routingKey) {
        String validatedQueue = validateName(queue, "Queue");
        String validatedExchange = validateName(exchange, "Exchange");
        String validatedRoutingKey = validateName(routingKey, "Routing key");

        Queue queueInstance = new Queue(validatedQueue, true, false, false);
        DirectExchange exchangeInstance = new DirectExchange(validatedExchange, true, false);
        Binding binding = BindingBuilder.bind(queueInstance)
                .to(exchangeInstance)
                .with(validatedRoutingKey);

        rabbitAdmin.removeBinding(binding);
    }

    private String validateName(String value, String resourceName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(resourceName + " name is required");
        }
        return value.trim();
    }

    private String validateExchangeType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Exchange type is required");
        }

        String normalizedType = type.trim().toLowerCase(Locale.ROOT);
        if (normalizedType.equals(ExchangeTypes.DIRECT)
                || normalizedType.equals(ExchangeTypes.TOPIC)
                || normalizedType.equals(ExchangeTypes.FANOUT)) {
            return normalizedType;
        }

        throw new IllegalArgumentException("Exchange type must be one of: direct, topic, fanout");
    }
}
