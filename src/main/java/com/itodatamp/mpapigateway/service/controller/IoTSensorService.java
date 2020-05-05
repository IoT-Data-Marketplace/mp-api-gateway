package com.itodatamp.mpapigateway.service.controller;

import com.itodatamp.mpapigateway.dao.HttpResponseDTO;
import com.itodatamp.mpapigateway.dao.IoTSensor;
import com.itodatamp.mpapigateway.service.kafka.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IoTSensorService {

    private final TopicService topicService;

    public IoTSensorService(TopicService topicService) {
        this.topicService = topicService;
    }

    public List<IoTSensor> getAllIoTSensors(int count) {
        return null;
    }

    public Optional<IoTSensor> getIoTSensorByContractAddress(String sensorContractAddress) {
        return Optional.ofNullable(IoTSensor.builder().sensorContractAddress(sensorContractAddress).build());
    }

    public HttpResponseDTO registerIoTSensor(final String sensorContractAddress) {
        return topicService.createTopic(sensorContractAddress);
    }
}
