package com.rootlab.junit.test.clients;

import com.rootlab.junit.test.clients.exchange.ExchangeClientProperties;
import com.rootlab.junit.test.clients.exchange.ExchangeRateClient;
import com.rootlab.junit.test.exception.ExchangeFailure;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateClientTest {
	private MockWebServer mockWebServer;
	private ExchangeRateClient exchangeRateClient;

	@BeforeEach
	void setupMockWebServer() {
		mockWebServer = new MockWebServer();

		ExchangeClientProperties properties = new ExchangeClientProperties();
		properties.setBaseUrl(mockWebServer.url("/").url().toString());
		properties.setApiKey("029db72cee377e4bfa1fa413");

		exchangeRateClient = new ExchangeRateClient(WebClient.create(), properties);
	}

	@Test
	void exchangeCurrency() {
		String json = "{\"conversion_rate\": 0.8412}";
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(200)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(json)
		);

		CurrencyUnit eur = Monetary.getCurrency("EUR");
		CurrencyUnit usd = Monetary.getCurrency("USD");

		BigDecimal rate = exchangeRateClient.getExchangeRate(eur, usd);
		Assertions.assertThat(rate.doubleValue()).isEqualTo(0.8412);
	}

	@Test
	void makesTheCorrectRequest() throws InterruptedException {
		String json = "{\"conversion_rate\": 0.8412}";
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(200)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(json)
		);

		exchangeRateClient.getExchangeRate(Monetary.getCurrency("EUR"), Monetary.getCurrency("GBP"));
		RecordedRequest request = mockWebServer.takeRequest();

		Assertions.assertThat(request.getMethod()).isEqualTo("GET");
		Assertions.assertThat(request.getPath()).isEqualTo("/v6/029db72cee377e4bfa1fa413/pair/EUR/GBP");
	}

	@Test
	void exchangeError() {
		String json = "{\"result\": \"error\"}";
		mockWebServer.enqueue(
				new MockResponse().setResponseCode(200)
						.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.setBody(json)
		);
		CurrencyUnit eur = Monetary.getCurrency("EUR");
		CurrencyUnit gbp = Monetary.getCurrency("GBP");
		assertThrows(ExchangeFailure.class, () ->
				exchangeRateClient.getExchangeRate(eur, gbp)
		);
	}

}