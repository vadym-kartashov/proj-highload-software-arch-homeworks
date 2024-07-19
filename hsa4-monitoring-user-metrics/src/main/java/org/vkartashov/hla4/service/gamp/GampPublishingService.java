package org.vkartashov.hla4.service.gamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vkartashov.hla4.service.gamp.dto.AirQualityInfoUpdatedEventDto;
import org.vkartashov.hla4.service.gamp.dto.GampCollectRequestDto;
import org.vkartashov.hla4.service.gamp.dto.GampEventDto;
import org.vkartashov.hla4.service.gamp.dto.GampEventDtoBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class GampPublishingService {

    private static final Logger LOG = LoggerFactory.getLogger(GampPublishingService.class);
    public static final String AIR_QUALITY_INFO_UPDATED_EVENT = "airQualityInfoUpdatedEvent";
    private static final int GAMP_BATCH_SIZE = 20;

    @Value("${gamp.api.clientId}")
    private String clientId;

    @Autowired
    private GampSimpleWebClient webClient;

    public void publishAirQualityInfoUpdatedEvents(List<AirQualityInfoUpdatedEventDto> eventDtos) {
        List<GampEventDto> eventsToPublish = new ArrayList<>();
        for (AirQualityInfoUpdatedEventDto eventDto : eventDtos) {
            GampEventDto gampEventDto = new GampEventDtoBuilder()
                    .withEventName(AIR_QUALITY_INFO_UPDATED_EVENT)
                    .withParam("apiId", eventDto.getApiId(), true)
                    .withParam("city", eventDto.getCity(), true)
                    .withParam("stationName", eventDto.getStationName(), true)
                    .withParam("airQualityIndex", eventDto.getAirQualityIndex(), true)
                    .withParam("humidity", eventDto.getHumidity(), true)
                    .withParam("pm10", eventDto.getPm10(), true)
                    .withParam("pm25", eventDto.getPm25(), true)
                    .withParam("temperature", eventDto.getTemperature(), true)
                    .build();
            eventsToPublish.add(gampEventDto);
            if (eventsToPublish.size() == GAMP_BATCH_SIZE) {
                publishBatch(eventsToPublish);
                eventsToPublish.clear();
            }
        }
        publishBatch(eventsToPublish);
    }

    private void publishBatch(List<GampEventDto> eventsToPublish) {
        GampCollectRequestDto gampEventDtoBatch = new GampCollectRequestDto();
        gampEventDtoBatch.setClientId(clientId);
        gampEventDtoBatch.setEvents(eventsToPublish);
        LOG.info("Publishing batch of " + eventsToPublish.size() + " events.");
        webClient.publishCollectionRequest(gampEventDtoBatch);
    }

}
