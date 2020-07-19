package com.itodatamp.mpapigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDTO {
    private String contractAddress;
    private String nonce;
    private String jwt;
}