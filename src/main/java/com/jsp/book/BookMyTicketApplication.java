package com.jsp.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BookMyTicketApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookMyTicketApplication.class, args);
	}
}