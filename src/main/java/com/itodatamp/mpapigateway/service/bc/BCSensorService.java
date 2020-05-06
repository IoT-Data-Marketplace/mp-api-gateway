package com.itodatamp.mpapigateway.service.bc;

import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class BCSensorService {

    OkHttpClient client = new OkHttpClient();

    @SneakyThrows
    public HttpResponseDTO fetchSensorForContractAddress(final String sensorContractAddress) {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .port(8070)
                .addPathSegment("sensors")
                .addQueryParameter("sensorContractAddress", sensorContractAddress)
                .build();

        log.info("Executing the request: " + httpUrl);

        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();

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
