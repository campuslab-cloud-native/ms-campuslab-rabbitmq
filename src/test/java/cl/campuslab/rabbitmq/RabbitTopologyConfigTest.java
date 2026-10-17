package cl.campuslab.rabbitmq;

import cl.campuslab.rabbitmq.config.MessagingNames;
import cl.campuslab.rabbitmq.config.RabbitTopologyConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RabbitTopologyConfigTest {

    @Test
    void staticTopologyShouldBeDeclared() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RabbitTopologyConfig.class)) {
            assertEquals(MessagingNames.DIRECT_EXCHANGE, context.getBean("cmdDirectExchange", DirectExchange.class).getName());
            assertEquals(MessagingNames.TOPIC_EXCHANGE, context.getBean("cmdTopicExchange", TopicExchange.class).getName());
            assertEquals(MessagingNames.DEAD_LETTER_EXCHANGE, context.getBean("cmdDeadLetterExchange", DirectExchange.class).getName());

            assertNotNull(context.getBean("emailQueue", Queue.class));
            assertNotNull(context.getBean("prepQueue", Queue.class));
            assertNotNull(context.getBean("voucherQueue", Queue.class));
            assertNotNull(context.getBean("emailDlqQueue", Queue.class));
            assertNotNull(context.getBean("prepDlqQueue", Queue.class));
            assertNotNull(context.getBean("voucherDlqQueue", Queue.class));

            assertNotNull(context.getBean("emailDirectBinding", Binding.class));
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
}
