package com.itodatamp.mpapigateway.service.mutation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.dto.SensorDTO;
import com.itodatamp.mpapigateway.service.bc.BCSensorService;
import com.itodatamp.mpapigateway.service.entity.SensorEntityService;
import com.itodatamp.mpapigateway.service.kafka.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final TopicService topicService;
    private final BCSensorService bcSensorService;
    private final SensorEntityService sensorEntityService;

    public List<SensorDTO> getAllSensors(int count) {
        return null;
    }

    public Optional<SensorDTO> getSensorByContractAddress(String sensorContractAddress) {
        return Optional.ofNullable(SensorDTO.builder().sensorContractAddress(sensorContractAddress).build());
    }

    @SneakyThrows
    public HttpResponseDTO activateSensor(final String sensorContractAddress) {
//        first we check if the contract really exists on the blockchain, if not we throw an exception
         HttpResponseDTO bcHttpResponseDTO = bcSensorService.fetchSensorForContractAddress(sensorContractAddress);
         if (HttpStatus.valueOf(bcHttpResponseDTO.getStatusCode()) != HttpStatus.OK) throw new Exception(bcHttpResponseDTO.getResponseBody());

//         if the contract is ok, we create a topic on kafka cluster so the sensor can start pushing the data
        HttpResponseDTO topicHttpResponseDTO = topicService.createTopic(sensorContractAddress);
        if (HttpStatus.valueOf(topicHttpResponseDTO.getStatusCode()) != HttpStatus.CREATED) throw new Exception(topicHttpResponseDTO.getResponseBody());

//         finally we store the sensor data in our database to improve the search speed
        SensorDTO sensorDTO = new ObjectMapper().readValue(bcHttpResponseDTO.getResponseBody(), SensorDTO.class);
        HttpResponseDTO sensorEntityHttpResponseDTO = sensorEntityService.saveSensor(sensorDTO);
        if (HttpStatus.valueOf(sensorEntityHttpResponseDTO.getStatusCode()) != HttpStatus.OK) throw new Exception(sensorEntityHttpResponseDTO.getResponseBody());

        return HttpResponseDTO.builder().statusCode(sensorEntityHttpResponseDTO.getStatusCode()).responseBody(sensorEntityHttpResponseDTO.getResponseBody()).build();
    }
}
