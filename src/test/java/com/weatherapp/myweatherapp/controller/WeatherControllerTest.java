package com.weatherapp.myweatherapp.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import jakarta.servlet.ServletException;

@SpringBootTest
@AutoConfigureMockMvc
public class WeatherControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Nested
	@DisplayName("Testing 'forecastByCity' Method")
	class ForecastByCityTest {
		
		@ParameterizedTest
		@ValueSource(strings = {"London", "Berlin", "Paris"})
		@DisplayName("Should Return City Weather Forecast Data If Valid City Name Provided")
		void shouldReturnCityWeatherForecastDataIfValidCityNameProvided(String cityName) throws Exception {
			final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/forecast/" + cityName);
			
			mockMvc.perform(builder)
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$").exists())
					.andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(4)))
					.andExpect(MockMvcResultMatchers.jsonPath("$.address").value(cityName))
					.andExpect(MockMvcResultMatchers.jsonPath("$.currentConditions").isNotEmpty());
		}
		
		@Test
		@DisplayName("Should Throw ServletException If Invalid Non-Empty City Name Provided")
		void shouldThrowServletExceptionIfInvalidNonEmptyCityNameProvided() throws Exception {
			assertThrows(ServletException.class, () -> {
				final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/forecast/sdfsfsdf");
				mockMvc.perform(builder);
			});
		}
		
		@Test
		@DisplayName("Should Display 404 Not Found If No City Name Provided")
		void shouldDisplay404NotFoundIfNoCityNameProvided() throws Exception {
			final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/forecast/");
			mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().is4xxClientError());
		}
		
		@Test
		@DisplayName("Should Display 404 Not Found If Invalid URI Specified")
		void shouldDisplay404NotFoundIfInvalidURISpecified() throws Exception {
			final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/forecast");
			mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().is4xxClientError());
		}
	}
	
	@Nested
	@DisplayName("Testing 'daylightHoursComparison' Method")
	class DaylightHoursComparisonTest {
		
		@ParameterizedTest
		@CsvSource({"London,Berlin", "Paris,Milan", "Oslo,Madrid"})
		@DisplayName("Should Compare Daylight Hours Of Two Cities If Valid City Names Provided")
		void shouldCompareDaylightHoursOfTwoCitiesIfValidCityNamesProvided(String cityName1, String cityName2) throws Exception {
			final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/daylightHoursComparison/" + cityName1 + "/" + cityName2);
			
			ResultActions resultActions = mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			MvcResult mvcResult = resultActions.andReturn();
		    String result = mvcResult.getResponse().getContentAsString();
		    
		    assertFalse(result.isEmpty());
		    assertTrue(result.contains("'" + cityName1 + "' has a longer daylight duration than '" + cityName2 + "'") 
		    		|| result.contains("'" + cityName2 + "' has a longer daylight duration than '" + cityName1 + "'")
		    		|| result.contains("Both '" + cityName1 + "' and '" + cityName2 + "' have the same daylight duration")
		    		|| result.contains("Both '" + cityName2 + "' and '" + cityName1 + "' have the same daylight duration")
		    );
		}
		
		@ParameterizedTest
		@CsvSource({"sdfsfsdf,Berlin", "Paris,sdfsfsdf", "sdfsfsdf,sdfsfsdf"})
		@DisplayName("Should Catch ServletException If At Least One Invalid Non-Empty City Name Provided")
		void shouldCatchServletExceptionIfAtLeastOneInvalidNonEmptyCityNameProvided(String cityName1, String cityName2) throws Exception {
			final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/daylightHoursComparison/" + cityName1 + "/" + cityName2);
			ResultActions resultActions = mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().is4xxClientError());
			MvcResult mvcResult = resultActions.andReturn();
		    String result = mvcResult.getResponse().getContentAsString();
		    
		    assertFalse(result.isEmpty());
		    assertTrue(result.contains("Invalid City Name Provided!"));
		}
		
		@ParameterizedTest
		@ValueSource(strings = {"/daylightHoursComparison/London/", "/daylightHoursComparison//London", "/daylightHoursComparison//"})
		@DisplayName("Should Display 404 Not Found If At Least One City Name Not Provided")
		void shouldDisplay404NotFoundIfAtLeastOneCityNameNotProvided(String uri) throws Exception {
			final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(uri);
			mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().is4xxClientError());
		}
		
		@ParameterizedTest
		@ValueSource(strings = {"London", "Berlin", "Paris"})
		@DisplayName("Should Display Same Daylight Hours If Duplicate City Names Provided")
		void shouldDisplaySameDaylightHoursIfDuplicateCityNamesProvided(String cityName) throws Exception {
			final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/daylightHoursComparison/" + cityName + "/" + cityName);
			
			ResultActions resultActions = mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
			MvcResult mvcResult = resultActions.andReturn();
		    String result = mvcResult.getResponse().getContentAsString();
		    
		    assertFalse(result.isEmpty());
		    assertFalse(result.contains("'" + cityName + "' has a longer daylight duration than '" + cityName + "'"));
		    assertTrue(result.contains("Both '" + cityName + "' and '" + cityName + "' have the same daylight duration"));
		}
		
		@ParameterizedTest
		@ValueSource(strings = {"/daylightHoursComparison", "/daylightHoursComparison/"})
		@DisplayName("Should Display 404 Not Found If Invalid URI Specified")
		void shouldDisplay404NotFoundIfInvalidURISpecified(String uri) throws Exception {
			final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(uri);
			mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().is4xxClientError());
		}
	}
}
