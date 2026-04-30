package com.personal.financialvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FinancialVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialVaultApplication.class, args);
	}

}
