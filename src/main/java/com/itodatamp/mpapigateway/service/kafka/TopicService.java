package com.itodatamp.mpapigateway.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.CreateTopicDTO;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {

    private final PropertiesBean properties;

    @SneakyThrows
    public HttpResponseDTO createTopic(String topicName, Headers tracingHeaders) {
        OkHttpClient client = new OkHttpClient();
        CreateTopicDTO createTopicDTO = CreateTopicDTO.builder().topicName(topicName).build();
        ObjectMapper mapper = new ObjectMapper();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mapper.writeValueAsString(createTopicDTO), mediaType);
        Request request = new Request.Builder()
                .url(properties.getKafkaRestProxyURL().concat("/topics"))
                .post(body)
                .headers(tracingHeaders)
                .addHeader("Content-Type", "application/json")
                .build();
        log.info("Creating a topic with name: " + createTopicDTO.getTopicName());
        Response response = client.newCall(request).execute();
        HttpResponseDTO httpResponseDTO = HttpResponseDTO.builder().statusCode(response.code()).responseBody(response.body().string()).build();
        log.debug("Response Code: " + HttpStatus.valueOf(httpResponseDTO.getStatusCode()));
        log.debug("Response Body: " + httpResponseDTO.getResponseBody());
        return httpResponseDTO;
    }

    @SneakyThrows
    public HttpResponseDTO getTopicSummary(String topicName, Headers tracingHeaders) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(properties.getKafkaRestProxyURL().concat("/topics/").concat(topicName))
                .headers(tracingHeaders)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        HttpResponseDTO httpResponseDTO = HttpResponseDTO.builder().statusCode(response.code()).responseBody(response.body().string()).build();
        log.debug("Response Code: " + HttpStatus.valueOf(httpResponseDTO.getStatusCode()));
        log.debug("Response Body: " + httpResponseDTO.getResponseBody());
        return httpResponseDTO;
    }
}
