package com.auction.auctionquery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AuctionqueryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionqueryServiceApplication.class, args);
	}

}
