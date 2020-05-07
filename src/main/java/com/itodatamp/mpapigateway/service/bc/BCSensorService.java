package com.itodatamp.mpapigateway.service.bc;

import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class BCSensorService {

    OkHttpClient client = new OkHttpClient();
    private final PropertiesBean properties;

    @SneakyThrows
    public HttpResponseDTO fetchSensorForContractAddress(final String sensorContractAddress) {

        URL url = new URL(properties.getBcClientURL()
                .concat("/sensors")
                .concat("?sensorContractAddress=")
                .concat(sensorContractAddress));

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        log.info("Submitting GET request: ".concat(url.toString()));
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpResponseDTO httpResponseDTO = HttpResponseDTO.builder().statusCode(response.code()).responseBody(response.body().string()).build();

        return httpResponseDTO;
    }

}
