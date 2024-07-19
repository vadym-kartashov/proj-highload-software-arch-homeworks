package org.vkartashov.hla4;

import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.util.CollectionUtils;
import org.vkartashov.hla4.service.gamp.GampPublishingService;
import org.vkartashov.hla4.service.gamp.dto.AirQualityInfoUpdatedEventDto;
import org.vkartashov.hla4.service.saveecobot.SaveEcoBotServiceClient;
import org.vkartashov.hla4.service.saveecobot.dto.EcoBotDataEntryDto;
import org.vkartashov.hla4.service.saveecobot.dto.PollutantDto;
import org.vkartashov.hla4.util.Utils;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static org.vkartashov.hla4.util.Constants.*;

@SpringBootApplication
public class Hla4Application implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(Hla4Application.class);

	@Autowired
	private SaveEcoBotServiceClient saveEcoBotServiceClient;

	@Autowired
	private GampPublishingService gampPublishingService;

	public static void main(String[] args) {
		new SpringApplicationBuilder(Hla4Application.class)
				.properties("spring.config.name:application,saveecobot,gamp")
				.web(WebApplicationType.NONE)
				.run(args);
	}

	// Entry point
	@Override
	public void run(String... args) {
		TimerTask task = new TimerTask() {
			public void run() {
				generateEcoBotAnalyticsData();
			}
		};
		Timer timer = new Timer("PollutedAirInKyivAnalyticsPushingTimer");
		long eachHourDelay = 1000 * 60 * 60;
		timer.scheduleAtFixedRate(task, 0,eachHourDelay);
	}

	private void generateEcoBotAnalyticsData() {
		LOG.info("Start worker");
		List<EcoBotDataEntryDto> entries = saveEcoBotServiceClient.fetchData();
		LOG.info("Fetched {} entries from SaveEcoBot", entries.size());
		List<EcoBotDataEntryDto> relevantEntries = entries.stream()
				.filter(this::isRelevantEcoBotDataEntry)
				.toList();
		LOG.info("Filtered {} entries from SaveEcoBot", entries.size() - relevantEntries.size());
		gampPublishingService.publishAirQualityInfoUpdatedEvents(
				relevantEntries.stream()
						.map(this::mapEcoDataToGampEvent)
						.collect(Collectors.toList())
		);
		LOG.info("Finish worker");
	}

	private boolean isRelevantEcoBotDataEntry(EcoBotDataEntryDto ecoBotDataEntryDto) {
		if (CollectionUtils.isEmpty(ecoBotDataEntryDto.getPollutants())) {
			return false;
		}
		return ecoBotDataEntryDto.getPollutants().stream().anyMatch(p -> {
			Date date = Utils.parseDate(p.getTime());
			boolean isOutdatedValue = date == null || date.before(DateUtils.addDays(new Date(), -1));
			boolean isEmptyValue = p.getValue() == null;
			return !isOutdatedValue && !isEmptyValue;
		});
	}



	private AirQualityInfoUpdatedEventDto mapEcoDataToGampEvent(EcoBotDataEntryDto ecoBotDataEntryDto) {
		AirQualityInfoUpdatedEventDto eventDto = new AirQualityInfoUpdatedEventDto();
		eventDto.setCity(ecoBotDataEntryDto.getCityName());
		eventDto.setApiId(ecoBotDataEntryDto.getId());
		eventDto.setStationName(ecoBotDataEntryDto.getStationName());
		for (PollutantDto pollutantDto : ecoBotDataEntryDto.getPollutants()) {
			Double value = pollutantDto.getValue();
			switch (pollutantDto.getPol()) {
				case POLLUTANT_TYPE_PM10 -> eventDto.setPm10(value);
				case POLLUTANT_TYPE_PM25 -> eventDto.setPm25(value);
				case POLLUTANT_TYPE_HUMIDITY -> eventDto.setHumidity(value);
				case POLLUTANT_TYPE_TEMPERATURE -> eventDto.setTemperature(value);
				case POLLUTANT_TYPE_AIR_QUALITY_INDEX -> eventDto.setAirQualityIndex(value);
			}
		}
		return eventDto;
	}

}
