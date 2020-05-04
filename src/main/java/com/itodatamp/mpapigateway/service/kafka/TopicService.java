package com.itodatamp.mpapigateway.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.CreateTopicDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final PropertiesBean properties;

    @SneakyThrows
    public void createTopic(String topicName) {
        OkHttpClient client = new OkHttpClient();
        CreateTopicDTO createTopicDTO = CreateTopicDTO.builder().topicName(topicName).build();
        ObjectMapper mapper = new ObjectMapper();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, mapper.writeValueAsString(createTopicDTO));
        Request request = new Request.Builder()
                .url(properties.getKafkaRestProxyURL() + "/topics")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
    }
}
