package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.service.SensorAlertService;
import com.algaworks.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

import static com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_ALERTING;
import static com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_PROCESS_TEMPERATURE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final TemperatureMonitoringService temperatureMonitoringService;
    private final SensorAlertService sensorAlertService;

    // @Payload para que ele serialize o payload da mensagem pra dentro desse objeto
    // concurrency = min-max de Threads que vao ser processadas por vez, se nao configurar processa uma a uma
    // O concurrency depende do Prefetch, porque ele limita a quantidade mensagens buscadas por vez
    @RabbitListener(queues = QUEUE_PROCESS_TEMPERATURE, concurrency = "2-3")
    @SneakyThrows
    public void handleProcessingTemperature(@Payload TemperatureLogData temperatureLogData,
                       @Headers Map<String, Object> headers) {

        // Logando:

        /*
        TSID sensorId = temperatureLogData.getSensorId();
        Double temperature = temperatureLogData.getValue();
        log.info("Temperatue updated: SensorId {} Temp {}", sensorId, temperature);
        log.info("Headers: {}", headers);
        */


        temperatureMonitoringService.processTemperatureReading(temperatureLogData);

        Thread.sleep(Duration.ofSeconds(10));
    }

    @RabbitListener(queues = QUEUE_ALERTING, concurrency = "2-3")
    @SneakyThrows
    public void handleAlerting(@Payload TemperatureLogData temperatureLogData,
                       @Headers Map<String, Object> headers) {

//        log.info("Alerting: SensorId {} Temperatura {}", temperatureLogData.getSensorId(), temperatureLogData.getValue());
        sensorAlertService.handleAlert(temperatureLogData);
        Thread.sleep(Duration.ofSeconds(10));
    }


}
