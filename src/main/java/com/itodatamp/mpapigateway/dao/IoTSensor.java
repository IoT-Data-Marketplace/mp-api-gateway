package com.itodatamp.mpapigateway.dao;

import com.itodatamp.mpapigateway.graphql.input.Geolocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IoTSensor {
    private String sensorContractAddress;
    private String dataStreamEntityContractAddress;
    private SensorType sensorType;
    private Geolocation geolocation;
}
