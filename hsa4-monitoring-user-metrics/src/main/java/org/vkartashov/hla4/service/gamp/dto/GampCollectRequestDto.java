package org.vkartashov.hla4.service.gamp.dto;

import lombok.Data;

import java.util.List;

@Data
public class GampCollectRequestDto {

    private String clientId;
    private List<GampEventDto> events;

}
