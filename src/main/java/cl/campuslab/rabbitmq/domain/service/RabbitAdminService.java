package cl.campuslab.rabbitmq.domain.service;

public interface RabbitAdminService {

    void createQueue(String name, boolean durable, String deadLetterExchange, String deadLetterRoutingKey);

    void deleteQueue(String name);

    void createExchange(String name, String type, boolean durable);

    void deleteExchange(String name);

    void createBinding(String queue, String exchange, String routingKey);

    void deleteBinding(String queue, String exchange, String routingKey);
}
