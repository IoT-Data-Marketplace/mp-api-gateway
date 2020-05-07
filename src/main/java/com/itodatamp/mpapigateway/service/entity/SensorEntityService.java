package com.itodatamp.mpapigateway.service.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.dto.SensorDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEntityService {

    private final PropertiesBean properties;

    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public HttpResponseDTO saveSensor(SensorDTO sensorDTO) {

        OkHttpClient client = new OkHttpClient();
        URL url = new URL(properties.getEntityManagerURL().concat("/sensors"));

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, mapper.writeValueAsString(sensorDTO));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        log.info("Submitting POST request, URL: ".concat(url.toString()).concat("\nBody: ").concat(mapper.writeValueAsString(sensorDTO)));

        Response response = client.newCall(request).execute();

        HttpResponseDTO httpResponseDTO = HttpResponseDTO.builder().statusCode(response.code()).responseBody(response.body().string()).build();
        return httpResponseDTO;
    }
}
