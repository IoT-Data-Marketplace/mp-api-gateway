package com.itodatamp.mpapigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SensorSummaryDTO {
    private String sensorContractAddress;
    private int streamSize;
    private String jwt;
}
