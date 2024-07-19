package org.vkartashov.hla4.service.saveecobot.dto;

import lombok.Data;

@Data
public class PollutantDto {
    private String pol;
    private String unit;
    private String time;
    private Double value;
    private String averaging;
}
