package com.itodatamp.mpapigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMessagesDTO {

    private List<ResponseMessagesDTO.Record> records;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Record {
        private String key;
        private String value;
        private int offset;
    }

}
