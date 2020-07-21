package com.itodatamp.mpapigateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.dto.*;
import com.itodatamp.mpapigateway.security.JWTTokenService;
import com.itodatamp.mpapigateway.service.bc.BCSensorService;
import com.itodatamp.mpapigateway.service.kafka.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final TopicService topicService;
    private final BCSensorService bcSensorService;
    private final JWTTokenService jwtTokenService;
    private final AuthEntityManagerService authEntityManagerService;
    ObjectMapper mapper = new ObjectMapper();

    public List<SensorDTO> getAllSensors(int count, Headers tracingHeaders) {
        return null;
    }

    public Optional<SensorDTO> getSensorByContractAddress(String sensorContractAddress, Headers tracingHeaders) {
        return Optional.ofNullable(SensorDTO.builder().sensorContractAddress(sensorContractAddress).build());
    }

    @SneakyThrows
    public HttpResponseDTO activateSensor(final String sensorContractAddress, Headers tracingHeaders) {
//        first we check if the contract really exists on the blockchain, if not we throw an exception
         HttpResponseDTO bcHttpResponseDTO = bcSensorService.fetchSensorForContractAddress(sensorContractAddress, tracingHeaders);
         if (HttpStatus.valueOf(bcHttpResponseDTO.getStatusCode()) != HttpStatus.OK) throw new Exception(bcHttpResponseDTO.getResponseBody());
        SensorDTO sensorDTO = new ObjectMapper().readValue(bcHttpResponseDTO.getResponseBody(), SensorDTO.class);

//         if the contract is ok, we create a topic on kafka cluster so the sensor can start pushing the data
        HttpResponseDTO topicHttpResponseDTO = topicService.createTopic(sensorContractAddress, tracingHeaders);
        if (HttpStatus.valueOf(topicHttpResponseDTO.getStatusCode()) != HttpStatus.CREATED) throw new Exception(topicHttpResponseDTO.getResponseBody());

        //        then set status to ACTIVE for the sensor
        sensorDTO.setSensorStatus(SensorStatus.ACTIVE);
        HttpResponseDTO bcSensorStatusResponseDTO = bcSensorService.setSensorStatus(sensorDTO, tracingHeaders);
        if (HttpStatus.valueOf(bcSensorStatusResponseDTO.getStatusCode()) != HttpStatus.OK) throw new Exception(bcSensorStatusResponseDTO.getResponseBody());


        // if everything ok so far, create a sensor JWT token used to authenticate when publishing the data
        String sensorJWTToken = jwtTokenService.permanent(
                Map.of("sensorContractAddress", sensorContractAddress)
        );

//         store the token for later authentication
        AuthDTO authDTO = AuthDTO.builder()
                .contractAddress(sensorContractAddress)
//                .jwt(sensorJWTToken)
                .build();
         HttpResponseDTO saveSensorJWTHttpResponseDTO = authEntityManagerService.saveAuthDTO(authDTO, tracingHeaders);

        if (HttpStatus.valueOf(saveSensorJWTHttpResponseDTO.getStatusCode()) != HttpStatus.OK) throw new Exception(saveSensorJWTHttpResponseDTO.getResponseBody());

        return HttpResponseDTO.builder()
                .statusCode(HttpStatus.OK.value())
                .responseBody(mapper.writeValueAsString(authDTO))
                .build();
    }

    @SneakyThrows
    public SensorSummaryDTO getSensorSummary(final String sensorContractAddress, Headers tracingHeaders) {

//         if the contract is ok, we create a topic on kafka cluster so the sensor can start pushing the data
        HttpResponseDTO topicHttpResponseDTO = topicService.getTopicSummary(sensorContractAddress, tracingHeaders);
//        if (HttpStatus.valueOf(topicHttpStatus.valueOf(httpResponseDTO.getStatusCode())) != HttpStatus.OK) throw new Exception(topicHttpResponseDTO.getResponseBody());

        JSONObject jsonObject = new JSONObject(topicHttpResponseDTO.getResponseBody());

        return SensorSummaryDTO.builder()
                .sensorContractAddress(sensorContractAddress)
                .streamSize(jsonObject.getInt("topicSize"))
                .jwt(authEntityManagerService.getJWTForEntity(sensorContractAddress, tracingHeaders))
                .build();
    }

}
