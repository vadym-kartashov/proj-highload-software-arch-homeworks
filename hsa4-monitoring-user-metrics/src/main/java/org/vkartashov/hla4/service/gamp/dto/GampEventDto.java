package org.vkartashov.hla4.service.gamp.dto;

import lombok.Data;

import java.util.Map;

@Data
public class GampEventDto {
    private String name;
    private Map<String, Object> params;
}
