package com.itodatamp.mpapigateway.service.bc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.dto.SensorDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class BCSensorService {

    private final PropertiesBean properties;

    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public HttpResponseDTO fetchSensorForContractAddress(final String sensorContractAddress, Headers tracingHeaders) {
        OkHttpClient client = new OkHttpClient();
        URL url = new URL(properties.getBcClientURL()
                .concat("/sensors")
                .concat("?sensorContractAddress=")
                .concat(sensorContractAddress));

        Request request = new Request.Builder()
                .headers(tracingHeaders)
                .url(url)
                .get()
                .build();

        log.debug("Submitting GET request: ".concat(url.toString()));
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpResponseDTO httpResponseDTO = HttpResponseDTO.builder().statusCode(response.code()).responseBody(response.body().string()).build();
        log.debug("Response Code: " + HttpStatus.valueOf(httpResponseDTO.getStatusCode()));
        log.debug("Response Body: " + httpResponseDTO.getResponseBody());
        return httpResponseDTO;
    }

    @SneakyThrows
    public HttpResponseDTO setSensorStatus(SensorDTO sensorDTO, Headers tracingHeaders) {
        URL url = new URL(properties.getBcClientURL()
                .concat("/sensors")
                .concat("/status"));

        // set higher timeout in order to wait long enough for the smart contract transaction to complete
        OkHttpClient client = new OkHttpClient().newBuilder()
                .callTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .connectTimeout(Duration.ofSeconds(60))
                .build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, mapper.writeValueAsString(sensorDTO));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(tracingHeaders)
                .addHeader("Content-Type", "application/json")
                .build();

        log.info("Submitting POST request, URL: ".concat(url.toString()).concat("\nBody: ").concat(mapper.writeValueAsString(sensorDTO)));

        Response response = client.newCall(request).execute();

        return HttpResponseDTO.builder().statusCode(response.code()).responseBody(response.body().string()).build();
    }

    @SneakyThrows
    public boolean isGivenEntitySensorOwner(String entityContractAddress, String sensorContractAddress, Headers tracingHeaders) {
        OkHttpClient client = new OkHttpClient();
        URL url = new URL(properties.getBcClientURL()
                .concat("/sensors/owner")
                .concat("?sensorContractAddress=")
                .concat(sensorContractAddress)
                .concat("&entityContractAddress=")
                .concat(entityContractAddress));

        Request request = new Request.Builder()
                .headers(tracingHeaders)
                .url(url)
                .get()
                .build();

        log.debug("Submitting GET request: ".concat(url.toString()));
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("Entity: ".concat(entityContractAddress).concat(" unauthorized to stream from sensor: ".concat(sensorContractAddress)));
        return Boolean.parseBoolean(response.body().string());
    }
}
