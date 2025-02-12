package com.weatherapp.myweatherapp.controller;

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

  // TODO: given two city names, check which city its currently raining in

  // Checks the current weather conditions in a particular city
  private String cityCurrentConditions(String city) {
	  ResponseEntity<CityInfo> cityInfoResponseEntity = forecastByCity(city);
	  JsonNode cityInfoJsonNode = new ObjectMapper().valueToTree(cityInfoResponseEntity.getBody());
	  return cityInfoJsonNode.get("currentConditions").get("conditions").asText();
  }
  
  // Takes two city names and checks if none, one, or both of the two corresponding cities are currently experiencing rain
  @GetMapping("/rainCheck/{city1}/{city2}")
  public ResponseEntity<String> rainCheck(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {
	  String regex = "(?i).*rain.*";
	  
	  String city1CurrentConditions = cityCurrentConditions(city1);
	  String city2CurrentConditions = cityCurrentConditions(city2);
	  
	  boolean itIsCurrentlyRainingInCity1 = city1CurrentConditions.matches(regex);
	  boolean itIsCurrentlyRainingInCity2 = city2CurrentConditions.matches(regex);
	  
	  String outputText = "<b>Result:</b><br/>";
	  if(itIsCurrentlyRainingInCity1 && itIsCurrentlyRainingInCity2) {
		  outputText += "It is currently raining in both '" + city1 + "' and '" + city2 + "'.";
	  }
	  else if(itIsCurrentlyRainingInCity1) {
		  outputText += "It is currently raining in '" + city1 + "', but not in '" + city2 + "'.";
	  }
	  else if(itIsCurrentlyRainingInCity2) {
		  outputText += "It is currently raining in '" + city2 + "', but not in '" + city1 + "'.";
	  }
	  else {
		  outputText += "It is currently not raining in either '" + city1 + "' or '" + city2 + "'.";
	  }
	  
	  outputText += "<br/><br/><b>Reason:</b><br/>Current conditions in '" + city1 + "': " + city1CurrentConditions + ".<br/>Current conditions in '" + city2 + "': " + city2CurrentConditions + ".";
	  return ResponseEntity.ok(outputText);
  }
}
