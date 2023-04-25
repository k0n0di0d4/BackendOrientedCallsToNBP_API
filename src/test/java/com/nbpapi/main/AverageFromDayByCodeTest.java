package com.nbpapi.main;

import com.nbpapi.main.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
class AverageFromDayByCodeTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	//should return 200, the date is correct
	@Test
	void correctShouldReturn_OK() {
		String currencyCode = "USD";
		String date = "2023-04-20";
		ResponseEntity<Double> response = testRestTemplate.getForEntity(
				"http://localhost:8080/averageExchangeRate/" + currencyCode + "/" + date, Double.class
		);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	void getAverageFromDayByCode_FreeDay_ReturnsNotFound() {
		String currencyCode = "USD";
		String date = "2023-01-01";
		ResponseEntity<Double> response = testRestTemplate.getForEntity(
				"http://localhost:8080/averageExchangeRate/" + currencyCode + "/" + date, Double.class
		);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		//assertNotNull(response.getBody());
	}

}
