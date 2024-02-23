package com.transperfect.machinetranslation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@ComponentScan(basePackages = {"com.*"})
@EnableScheduling
public class MachineTranslationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MachineTranslationApplication.class, args);
	}

}
