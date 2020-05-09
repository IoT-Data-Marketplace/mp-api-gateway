package com.itodatamp.mpapigateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.dto.NewMessagesDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import okhttp3.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log
public class MessageService {

    private final PropertiesBean properties;
    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public HttpResponseDTO sendMessages(String sensorContractAddress, NewMessagesDTO messagesDTO) {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, mapper.writeValueAsString(messagesDTO));
        Request request = new Request.Builder()
                .url(properties.getKafkaRestProxyURL().concat("/messages/").concat(sensorContractAddress))
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        HttpResponseDTO httpResponseDTO = HttpResponseDTO.builder().statusCode(response.code()).responseBody(response.body().string()).build();
        log.info("Response Code: " + httpResponseDTO.getStatusCode());
        log.info("Response Body: " + httpResponseDTO.getResponseBody());
        return httpResponseDTO;
    }
}
