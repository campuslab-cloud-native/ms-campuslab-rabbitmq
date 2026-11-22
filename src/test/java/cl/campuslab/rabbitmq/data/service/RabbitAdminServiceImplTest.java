package cl.campuslab.rabbitmq.data.service;

import cl.campuslab.rabbitmq.domain.service.RabbitAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitAdminServiceImplTest {

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
