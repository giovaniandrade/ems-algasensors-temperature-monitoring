package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorMonitoringOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/sensors/{sensorId}/monitoring")
@RequiredArgsConstructor
public class SensorMonitoringController {

    private final SensorMonitoringRepository sensorMonitoringRepository;

    @GetMapping
    public SensorMonitoringOutput getDetails(@PathVariable TSID sensorId) {
        SensorMonitoring sensorMonitoring = findByIdOrDefault(sensorId);
        return SensorMonitoringOutput.builder()
                .id(sensorMonitoring.getId().getValue())
                .lastTemperature(sensorMonitoring.getLastTemperature())
                .updatedAt(sensorMonitoring.getUpdatedAt())
                .enabled(sensorMonitoring.getEnabled())
                .build();
    }

    @PutMapping("/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable TSID sensorId) {
        SensorMonitoring sensorMonitoring = findByIdOrDefault(sensorId);

        if (sensorMonitoring.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        sensorMonitoring.setEnabled(true);
        sensorMonitoringRepository.saveAndFlush(sensorMonitoring);
    }

    // Não gostei muito dessa abordagem
    @DeleteMapping("/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SneakyThrows // Isso é necessário para o Thread.sleep funcionar sem precisar declarar a exceção
    public void disable(@PathVariable TSID sensorId) {
        SensorMonitoring sensorMonitoring = findByIdOrDefault(sensorId);

        if (!sensorMonitoring.getEnabled()) {
            Thread.sleep(Duration.ofSeconds(10));
        }

        sensorMonitoring.setEnabled(false);
        sensorMonitoringRepository.saveAndFlush(sensorMonitoring);
    }

    private SensorMonitoring findByIdOrDefault(TSID sensorId) {
        return sensorMonitoringRepository.findById(new SensorId(sensorId))
                .orElse(SensorMonitoring.builder()
                        .id(new SensorId(sensorId))
                        .enabled(false)
                        .lastTemperature(null)
                        .updatedAt(null)
                        .build());
    }

}
