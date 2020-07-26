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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

import static com.itodatamp.mpapigateway.constants.Contants.AUTH_SENSOR_PUBLISHING;

@Service
@RequiredArgsConstructor
@Log
public class MessageService {

    private final AuthService authService;
    private final PropertiesBean properties;
    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public HttpResponseDTO publishMessages(String sensorContractAddress, String jwt, NewMessagesDTO messagesDTO, Headers tracingHeaders) {
        if (!authService.isJWTValid(sensorContractAddress, AUTH_SENSOR_PUBLISHING, jwt, tracingHeaders))
            return HttpResponseDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).responseBody("JWT Token not valid").build();

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
        return HttpResponseDTO.builder().statusCode(response.code()).responseBody(response.body().string()).build();
    }

    @SneakyThrows
    public ResponseMessagesDTO getMessagesForSensor(String entityContractAddress, String sensorContractAddress, String jwt, Integer offset, Integer count, Headers tracingHeaders) {
        if (!authService.canStreamMessages(entityContractAddress, sensorContractAddress, jwt, tracingHeaders))
            return ResponseMessagesDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).records(new LinkedList<>()).build();


        ResponseMessagesDTO responseMessagesDTO = ResponseMessagesDTO.builder().statusCode(HttpStatus.OK.value()).records(new LinkedList<>()).build();

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
