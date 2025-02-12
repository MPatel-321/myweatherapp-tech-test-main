package com.weatherapp.myweatherapp.controller;

import java.time.Duration;
import java.time.LocalTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WeatherController {

  @Autowired
  WeatherService weatherService;

  @GetMapping("/forecast/{city}")
  public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {

    CityInfo ci = weatherService.forecastByCity(city);

    return ResponseEntity.ok(ci);
  }

  // TODO: given two city names, compare the length of the daylight hours and return the city with the longest day

  // Calculates the duration between two times
  private Duration durationBetweenTwoTimes(String time1, String time2) {
	  LocalTime time1LocalTime = LocalTime.parse(time1);
	  LocalTime time2LocalTime = LocalTime.parse(time2);
	  return Duration.between(time1LocalTime, time2LocalTime);
  }
  
  // Calculates the daylight duration of a particular city (Note: city daylight duration = city sunset time - city sunrise time)
  private Duration calculateCityDaylightDuration(String city) {
	  ResponseEntity<CityInfo> cityInfoResponseEntity = forecastByCity(city);
	  JsonNode cityInfoJsonNode = new ObjectMapper().valueToTree(cityInfoResponseEntity.getBody());
	  
	  String citySunriseTime = cityInfoJsonNode.get("currentConditions").get("sunrise").asText();
	  String citySunsetTime = cityInfoJsonNode.get("currentConditions").get("sunset").asText();
	  
	  return durationBetweenTwoTimes(citySunriseTime, citySunsetTime);
  }
  
  // Takes two city names and determines which of the two corresponding cities has the longer daylight duration (Note: city daylight duration = city sunset time - city sunrise time)
  @GetMapping("/daylightHoursComparison/{city1}/{city2}")
  public ResponseEntity<String> daylightHoursComparison(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {
	  Duration city1DaylightDuration = calculateCityDaylightDuration(city1);
	  Duration city2DaylightDuration = calculateCityDaylightDuration(city2);
	  
	  int city1AndCity2DaylightDurationComparison = city1DaylightDuration.compareTo(city2DaylightDuration);
	  
	  String outputText = "<b>Result:</b><br/>";
	  if(city1AndCity2DaylightDurationComparison > 0) {
		  outputText += "'" + city1 + "' has a longer daylight duration than '" + city2 + "'.";
	  }
	  else if(city1AndCity2DaylightDurationComparison == 0) {
		  outputText += "Both '" + city1 + "' and '" + city2 + "' have the same daylight duration.";
	  }
	  else {
		  outputText += "'" + city2 + "' has a longer daylight duration than '" + city1 + "'.";
	  }
	  
	  outputText += "<br/><br/><b>Reason:</b><br/>Daylight duration in '" + city1 + "': " + String.valueOf(city1DaylightDuration.toHoursPart()) + " Hours, " + String.valueOf(city1DaylightDuration.toMinutesPart()) + " Minutes, " + String.valueOf(city1DaylightDuration.toSecondsPart()) + " Seconds.<br/>Daylight duration in '" + city2 + "': " + String.valueOf(city2DaylightDuration.toHoursPart()) + " Hours, " + String.valueOf(city2DaylightDuration.toMinutesPart()) + " Minutes, " + String.valueOf(city2DaylightDuration.toSecondsPart()) + " Seconds.";
	  return ResponseEntity.ok(outputText);
  }
  
  // TODO: given two city names, check which city its currently raining in

}
