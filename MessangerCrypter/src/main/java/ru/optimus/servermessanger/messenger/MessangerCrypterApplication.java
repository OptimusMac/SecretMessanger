package ru.optimus.servermessanger.messenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MessangerCrypterApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessangerCrypterApplication.class, args);
	}

}
