package org.vkartashov.hla4.service.saveecobot.dto;

import lombok.Data;

import java.util.List;

@Data
public class EcoBotDataEntryDto {

    private String id;
    private String cityName;
    private String stationName;
    private String localName;
    private String timezone;
    private String latitude;
    private String longitude;
    private List<PollutantDto> pollutants;
    private String platformName;

}