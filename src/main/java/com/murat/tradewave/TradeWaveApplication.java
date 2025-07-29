package com.murat.tradewave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.murat.tradewave.model")
public class TradeWaveApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeWaveApplication.class, args);
	}

}
