package com.itodatamp.mpapigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorDTO {
    private String sensorContractAddress;
    private String dataStreamEntityContractAddress;
    private SensorType sensorType;
    private Double latitude;
    private Double longitude;
    private SensorStatus sensorStatus;
}
