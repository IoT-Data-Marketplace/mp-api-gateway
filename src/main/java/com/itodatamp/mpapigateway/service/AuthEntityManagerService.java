package com.itodatamp.mpapigateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.AuthDTO;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthEntityManagerService {

    private final PropertiesBean properties;
    ObjectMapper mapper = new ObjectMapper();
    MediaType mediaType = MediaType.parse("application/json");
    OkHttpClient client = new OkHttpClient();

    @SneakyThrows
    public HttpResponseDTO saveAuthDTO(AuthDTO authDTO, Headers tracingHeaders) {
        URL url = new URL(properties.getEntityManagerURL().concat("/auth"));
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
    public String getJWTForEntity(String entityContractAddress, Headers tracingHeaders) {
        Request request = new Request.Builder()
                .url(new URL(properties.getEntityManagerURL()
                        .concat("/auth/jwt")
                        .concat("?entityContractAddress=")
                        .concat(entityContractAddress)
                ))
                .get()
                .headers(tracingHeaders)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
