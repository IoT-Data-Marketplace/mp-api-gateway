package com.itodatamp.mpapigateway.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewMessagesDTO {

    private List<Record> records;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Record {
        private String key;
        private String value;
    }

}
