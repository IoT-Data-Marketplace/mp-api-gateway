package com.itodatamp.mpapigateway.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Geolocation {
    private String latitude;
    private String longitude;
}
