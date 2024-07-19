package org.vkartashov.hla4.service.gamp.dto;

import lombok.Data;

@Data
public class AirQualityInfoUpdatedEventDto {

    private String apiId;
    private String city;
    private String stationName;
    private Double airQualityIndex;
    private Double humidity;
    private Double pm10;
    private Double pm25;
    private Double temperature;

}
