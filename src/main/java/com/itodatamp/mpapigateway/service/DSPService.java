package com.itodatamp.mpapigateway.service;

import com.itodatamp.mpapigateway.config.PropertiesBean;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DSPService {

    private final PropertiesBean properties;

    @SneakyThrows
    public ResponseEntity<String> getChallenge(String dspAccountAddress, String dspContractAddress) {
        OkHttpClient client = new OkHttpClient();
        URL url = new URL(properties.getBcClientURL()
                .concat("/dsp")
                .concat("?dspContractAddress=")
                .concat(dspContractAddress)
                .concat("&dspAccountAddress=")
                .concat(dspAccountAddress));

        Request request = new Request.Builder()
//                .headers(tracingHeaders)
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

        if (!Boolean.parseBoolean(response.body().string()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();



        return ResponseEntity.ok("nonce");
    }

    @SneakyThrows
    public ResponseEntity<String> verifyChallenge(Map<String, Object> payload) {
        String signature = payload.get("signature").toString();
        String nonce = payload.get("nonce").toString();
        String dspAccountAddress = payload.get("dspAccountAddress").toString();
        String dspContractAddress = payload.get("dspContractAddress").toString();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("nonce", nonce);
        jsonObject.put("publicAddress", dspAccountAddress);
        jsonObject.put("signature", signature);

        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url("http://localhost:8030/api/verify")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();

        return ResponseEntity.ok(response.body().string());
    }
}
