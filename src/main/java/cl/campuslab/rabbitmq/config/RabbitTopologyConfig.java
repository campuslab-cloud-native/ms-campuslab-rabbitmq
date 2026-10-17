package cl.campuslab.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfig {

    @Bean
    public DirectExchange cmdDirectExchange() {
        return new DirectExchange(MessagingNames.DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange cmdTopicExchange() {
        return new TopicExchange(MessagingNames.TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange cmdDeadLetterExchange() {
        return new DirectExchange(MessagingNames.DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(MessagingNames.QUEUE_EMAIL)
                .withArgument("x-dead-letter-exchange", MessagingNames.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", MessagingNames.DLQ_ROUTING_KEY_EMAIL)
                .build();
    }

    @Bean
    public Queue prepQueue() {
        return QueueBuilder.durable(MessagingNames.QUEUE_PREP)
                .withArgument("x-dead-letter-exchange", MessagingNames.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", MessagingNames.DLQ_ROUTING_KEY_PREP)
                .build();
    }

    @Bean
    public Queue voucherQueue() {
        return QueueBuilder.durable(MessagingNames.QUEUE_VOUCHER)
                .withArgument("x-dead-letter-exchange", MessagingNames.DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", MessagingNames.DLQ_ROUTING_KEY_VOUCHER)
                .build();
    }

    @Bean
    public Queue emailDlqQueue() {
        return QueueBuilder.durable(MessagingNames.QUEUE_EMAIL_DLQ).build();
    }

    @Bean
    public Queue prepDlqQueue() {
        return QueueBuilder.durable(MessagingNames.QUEUE_PREP_DLQ).build();
    }

    @Bean
    public Queue voucherDlqQueue() {
        return QueueBuilder.durable(MessagingNames.QUEUE_VOUCHER_DLQ).build();
    }

    @Bean
    public Binding emailDirectBinding(Queue emailQueue, DirectExchange cmdDirectExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(cmdDirectExchange)
                .with(MessagingNames.ROUTING_KEY_EMAIL_DIRECT);
    }

    @Bean
    public Binding emailTopicBinding(Queue emailQueue, TopicExchange cmdTopicExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(cmdTopicExchange)
                .with(MessagingNames.ROUTING_KEY_EMAIL_TOPIC);
    }

    @Bean
    public Binding prepDirectBinding(Queue prepQueue, DirectExchange cmdDirectExchange) {
        return BindingBuilder.bind(prepQueue)
                .to(cmdDirectExchange)
                .with(MessagingNames.ROUTING_KEY_PREP_DIRECT);
    }

    @Bean
    public Binding prepTopicBinding(Queue prepQueue, TopicExchange cmdTopicExchange) {
        return BindingBuilder.bind(prepQueue)
                .to(cmdTopicExchange)
                .with(MessagingNames.ROUTING_KEY_PREP_TOPIC);
    }

    @Bean
    public Binding voucherDirectBinding(Queue voucherQueue, DirectExchange cmdDirectExchange) {
        return BindingBuilder.bind(voucherQueue)
                .to(cmdDirectExchange)
                .with(MessagingNames.ROUTING_KEY_VOUCHER_DIRECT);
    }

    @Bean
    public Binding voucherTopicBinding(Queue voucherQueue, TopicExchange cmdTopicExchange) {
        return BindingBuilder.bind(voucherQueue)
                .to(cmdTopicExchange)
                .with(MessagingNames.ROUTING_KEY_VOUCHER_TOPIC);
    }

    @Bean
    public Binding emailDeadLetterBinding(Queue emailDlqQueue, DirectExchange cmdDeadLetterExchange) {
        return BindingBuilder.bind(emailDlqQueue)
                .to(cmdDeadLetterExchange)
                .with(MessagingNames.DLQ_ROUTING_KEY_EMAIL);
    }

    @Bean
    public Binding prepDeadLetterBinding(Queue prepDlqQueue, DirectExchange cmdDeadLetterExchange) {
        return BindingBuilder.bind(prepDlqQueue)
                .to(cmdDeadLetterExchange)
                .with(MessagingNames.DLQ_ROUTING_KEY_PREP);
    }

    @Bean
    public Binding voucherDeadLetterBinding(Queue voucherDlqQueue, DirectExchange cmdDeadLetterExchange) {
        return BindingBuilder.bind(voucherDlqQueue)
                .to(cmdDeadLetterExchange)
                .with(MessagingNames.DLQ_ROUTING_KEY_VOUCHER);
    }
}
