package cl.campuslab.rabbitmq.data.service;

import cl.campuslab.rabbitmq.domain.service.RabbitAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class RabbitAdminServiceImplTest {

    @Test
    void createsQueueWithDeadLetterConfiguration() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        RabbitAdminService service = new RabbitAdminServiceImpl(rabbitAdmin);

        service.createQueue(" queue ", true, " dead.exchange ", " dead.route ");

        var queueCaptor = forClass(Queue.class);
        verify(rabbitAdmin).declareQueue(queueCaptor.capture());
        Queue queue = queueCaptor.getValue();
        assertEquals("queue", queue.getName());
        assertEquals(true, queue.isDurable());
        assertEquals("dead.exchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("dead.route", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void rejectsDeadLetterRoutingKeyWithoutExchange() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        RabbitAdminService service = new RabbitAdminServiceImpl(rabbitAdmin);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createQueue("queue", true, null, "dead.route")
        );

        verifyNoInteractions(rabbitAdmin);
    }

    @Test
    void createsSupportedExchangeTypesCaseInsensitively() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        RabbitAdminService service = new RabbitAdminServiceImpl(rabbitAdmin);

        service.createExchange(" direct ", " DIRECT ", true);
        service.createExchange("topic", "topic", false);
        service.createExchange("fanout", "fanout", true);

        var exchangeCaptor = forClass(Exchange.class);
        verify(rabbitAdmin, org.mockito.Mockito.times(3)).declareExchange(exchangeCaptor.capture());
        var exchanges = exchangeCaptor.getAllValues();
        assertInstanceOf(DirectExchange.class, exchanges.get(0));
        assertInstanceOf(TopicExchange.class, exchanges.get(1));
        assertInstanceOf(FanoutExchange.class, exchanges.get(2));
        assertEquals("direct", exchanges.get(0).getName());
        assertEquals("topic", exchanges.get(1).getName());
        assertEquals("fanout", exchanges.get(2).getName());
    }

    @Test
    void rejectsUnsupportedExchangeTypeWithoutDeclaringExchange() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        RabbitAdminService service = new RabbitAdminServiceImpl(rabbitAdmin);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createExchange("exchange", "headers", true)
        );

        verify(rabbitAdmin, never()).declareExchange(any(Exchange.class));
    }

    @Test
    void deletesQueueAndExchangeUsingTrimmedNames() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        RabbitAdminService service = new RabbitAdminServiceImpl(rabbitAdmin);

        service.deleteQueue(" queue ");
        service.deleteExchange(" exchange ");

        verify(rabbitAdmin).deleteQueue("queue");
        verify(rabbitAdmin).deleteExchange("exchange");
    }

    @Test
    void rejectsBlankNamesBeforeCallingRabbitAdmin() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        RabbitAdminService service = new RabbitAdminServiceImpl(rabbitAdmin);

        assertThrows(IllegalArgumentException.class, () -> service.deleteQueue("  "));

        verifyNoInteractions(rabbitAdmin);
    }

    @Test
    void createsBindingWithoutAssumingExchangeType() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        RabbitAdminService service = new RabbitAdminServiceImpl(rabbitAdmin);

        service.createBinding("queue", "topic.exchange", "booking.*");

        var bindingCaptor = forClass(Binding.class);
        verify(rabbitAdmin).declareBinding(bindingCaptor.capture());
        assertEquals("queue", bindingCaptor.getValue().getDestination());
        assertEquals("topic.exchange", bindingCaptor.getValue().getExchange());
        assertEquals("booking.*", bindingCaptor.getValue().getRoutingKey());
    }

    @Test
    void removesBindingWithoutAssumingExchangeType() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        RabbitAdminService service = new RabbitAdminServiceImpl(rabbitAdmin);

        service.deleteBinding("queue", "topic.exchange", "booking.*");

        var bindingCaptor = forClass(Binding.class);
        verify(rabbitAdmin).removeBinding(bindingCaptor.capture());
        assertEquals("queue", bindingCaptor.getValue().getDestination());
        assertEquals("topic.exchange", bindingCaptor.getValue().getExchange());
        assertEquals("booking.*", bindingCaptor.getValue().getRoutingKey());
    }
}
