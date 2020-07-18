package com.itodatamp.mpapigateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.dto.NewMessagesDTO;
import com.itodatamp.mpapigateway.dto.ResponseMessagesDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import okhttp3.*;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
@Log
public class MessageService {

    private final PropertiesBean properties;
    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public HttpResponseDTO sendMessages(String sensorContractAddress, NewMessagesDTO messagesDTO, Headers tracingHeaders) {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, mapper.writeValueAsString(messagesDTO));
        Request request = new Request.Builder()
                .url(properties.getKafkaRestProxyURL().concat("/messages/").concat(sensorContractAddress))
                .post(body)
                .headers(tracingHeaders)
                .addHeader("Content-Type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        HttpResponseDTO httpResponseDTO = HttpResponseDTO.builder().statusCode(response.code()).responseBody(response.body().string()).build();
        log.info("Response Code: " + httpResponseDTO.getStatusCode());
        log.info("Response Body: " + httpResponseDTO.getResponseBody());
        return httpResponseDTO;
    }

    @SneakyThrows
    public ResponseMessagesDTO getMessagesForSensor(String sensorContractAddress, Integer offset, Integer count, Headers tracingHeaders) {
        ResponseMessagesDTO responseMessagesDTO = ResponseMessagesDTO.builder().records(new LinkedList<>()).build();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .headers(tracingHeaders)
                .url(properties.getKafkaRestProxyURL().concat("/messages/").concat(sensorContractAddress).concat("?offset=").concat(String.valueOf(offset).concat("&count=").concat(String.valueOf(count))))
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        JSONArray jsonArray = new JSONArray(response.body().string());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            responseMessagesDTO.getRecords().add(
                    ResponseMessagesDTO.Record.builder()
                            .key(obj.getString("key"))
                            .value(obj.getString("value").replace("0C", "")) // todo this is just because of the way how kafka stores the values
                            .offset(obj.getInt("offset"))
                            .build()
            );
        }
        return responseMessagesDTO;
    }
}
