package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQInitializer {

    private final RabbitAdmin rabbitAdmin;

    // PostConstruct faz esse método ser chamado automaticamente após a construção do bean, garantindo que as filas,
    //  exchanges e bindings sejam criados antes de qualquer mensagem ser processada.
    @PostConstruct
    public void init() {
        // Chamando esse método ele vai criar as queues, exchanges e bindings que foram configurados como Bean.
        rabbitAdmin.initialize();
    }
}
