package org.vkartashov.hla4.service.gamp.dto;

import java.util.HashMap;
import java.util.Map;

public class GampEventDtoBuilder {
    private String eventName;
    private Map<String, Object> params;

    public GampEventDtoBuilder() {
    }

    public GampEventDtoBuilder withEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public GampEventDtoBuilder withParam(String key, Object value, boolean ignoreIfNull) {
        if (ignoreIfNull && value == null) {
            return this;
        }
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public GampEventDtoBuilder withParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public GampEventDto build() {
        GampEventDto gampEventDto = new GampEventDto();
        gampEventDto.setName(eventName);
        gampEventDto.setParams(params);
        return gampEventDto;
    }
}
