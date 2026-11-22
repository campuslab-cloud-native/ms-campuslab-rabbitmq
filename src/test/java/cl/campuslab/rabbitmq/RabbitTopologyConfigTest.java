package cl.campuslab.rabbitmq;

import cl.campuslab.rabbitmq.config.MessagingNames;
import cl.campuslab.rabbitmq.config.RabbitTopologyConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RabbitTopologyConfigTest {

    @Test
    void staticTopologyShouldBeDeclared() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RabbitTopologyConfig.class, TestRabbitConfiguration.class)) {
            assertEquals(MessagingNames.DIRECT_EXCHANGE, context.getBean("cmdDirectExchange", DirectExchange.class).getName());
            assertEquals(MessagingNames.TOPIC_EXCHANGE, context.getBean("cmdTopicExchange", TopicExchange.class).getName());
            assertEquals(MessagingNames.DEAD_LETTER_EXCHANGE, context.getBean("cmdDeadLetterExchange", DirectExchange.class).getName());

            Queue emailQueue = context.getBean("emailQueue", Queue.class);
            assertEquals(MessagingNames.QUEUE_EMAIL, emailQueue.getName());
            assertEquals(true, emailQueue.isDurable());
            assertEquals(
                    MessagingNames.DEAD_LETTER_EXCHANGE,
                    emailQueue.getArguments().get("x-dead-letter-exchange")
            );
            assertEquals(
                    MessagingNames.DLQ_ROUTING_KEY_EMAIL,
                    emailQueue.getArguments().get("x-dead-letter-routing-key")
            );
            assertNotNull(context.getBean("prepQueue", Queue.class));
            assertNotNull(context.getBean("voucherQueue", Queue.class));
            assertNotNull(context.getBean("emailDlqQueue", Queue.class));
            assertNotNull(context.getBean("prepDlqQueue", Queue.class));
            assertNotNull(context.getBean("voucherDlqQueue", Queue.class));

            Binding emailDirectBinding = context.getBean("emailDirectBinding", Binding.class);
            assertEquals(MessagingNames.QUEUE_EMAIL, emailDirectBinding.getDestination());
            assertEquals(MessagingNames.DIRECT_EXCHANGE, emailDirectBinding.getExchange());
            assertEquals(MessagingNames.ROUTING_KEY_EMAIL_DIRECT, emailDirectBinding.getRoutingKey());
            assertNotNull(context.getBean("emailTopicBinding", Binding.class));
            assertNotNull(context.getBean("prepDirectBinding", Binding.class));
            assertNotNull(context.getBean("prepTopicBinding", Binding.class));
            assertNotNull(context.getBean("voucherDirectBinding", Binding.class));
            assertNotNull(context.getBean("voucherTopicBinding", Binding.class));
            assertNotNull(context.getBean("emailDeadLetterBinding", Binding.class));
            assertNotNull(context.getBean("prepDeadLetterBinding", Binding.class));
            assertNotNull(context.getBean("voucherDeadLetterBinding", Binding.class));
        }
    }

    @TestConfiguration
    static class TestRabbitConfiguration {
        @Bean
        ConnectionFactory connectionFactory() {
            return new CachingConnectionFactory("localhost");
        }
    }
}
