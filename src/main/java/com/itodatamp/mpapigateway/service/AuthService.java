package com.itodatamp.mpapigateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.AuthDTO;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.service.bc.BCSensorService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Objects;

import static com.itodatamp.mpapigateway.constants.Contants.AUTH_SENSOR_SUBSCRIPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final BCSensorService bcSensorService;
    private final PropertiesBean properties;
    ObjectMapper mapper = new ObjectMapper();
    MediaType mediaType = MediaType.parse("application/json");
    OkHttpClient client = new OkHttpClient();

    @SneakyThrows
    public HttpResponseDTO saveAuthDTO(AuthDTO authDTO, String authPath, Headers tracingHeaders) {
        URL url = new URL(properties.getEntityManagerURL().concat(authPath));
        RequestBody dspBody = RequestBody.create(mediaType, mapper.writeValueAsString(authDTO));
        Request dspRequest = new Request.Builder()
                .url(url)
                .post(dspBody)
                .addHeader("Content-Type", "application/json")
                .headers(tracingHeaders)
                .build();
        Response response = client.newCall(dspRequest).execute();
        return HttpResponseDTO.builder()
                .statusCode(response.code())
                .responseBody(response.body().string())
                .build();
    }

    @SneakyThrows
    public String getJWTForEntity(String entityContractAddress, String authPath, Headers tracingHeaders) {
        Request request = new Request.Builder()
                .url(new URL(properties.getEntityManagerURL()
                        .concat(authPath)
                        .concat("/jwt")
                        .concat("?entityContractAddress=")
                        .concat(entityContractAddress)
                ))
                .get()
                .headers(tracingHeaders)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @SneakyThrows
    public boolean isJWTValid(String entityContractAddress, String authPath, String jwt, Headers tracingHeaders) {
        Request request = new Request.Builder()
                .url(new URL(properties.getEntityManagerURL()
                        .concat(authPath)
                        .concat("/valid")
                        .concat("?entityContractAddress=")
                        .concat(entityContractAddress)
                        .concat("&jwt=")
                        .concat(jwt)
                ))
                .get()
                .headers(tracingHeaders)
                .build();

        Response response = client.newCall(request).execute();
        return Boolean.parseBoolean(Objects.requireNonNull(response.body()).string());
    }


    public boolean canStreamMessages(String entityContractAddress, String sensorContractAddress, String jwt, Headers tracingHeaders) {
        boolean result = false;
        // first check if the jwt is valid
        if (!isJWTValid(entityContractAddress, AUTH_SENSOR_SUBSCRIPTION, jwt, tracingHeaders)) {
            // now we check if the entity is the sensor owner since they should be able to stream their messages without subscription
            if (bcSensorService.isGivenEntitySensorOwner(entityContractAddress, sensorContractAddress, tracingHeaders)) {
                // save the key-pair combination for the subsequent requests
                saveAuthDTO(AuthDTO.builder().contractAddress(sensorContractAddress).jwt(jwt).build(), AUTH_SENSOR_SUBSCRIPTION, tracingHeaders);
                result = true;
            }
        } else result = true;
        return result;
    }

}
