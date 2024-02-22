package com.fintech.sabiBank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info =@Info(
				title = "sabiBank App.",
				description = "Backend RestAPI developed using Spring Boot, designed to facilitate fundamental banking operations. The application provides a reliable and efficient platform for users to perform transactions, check balances, and retrieve account details seamlessly.\n",
				version = "v1.0",
				contact = @Contact(
						name = "Marvellous Giegbefumwen",
						email = "igmarvvvi@gmail.com",
						url = "https://github.com/gurumarv/sabiBank.git"
				),
				license = @License(
						name = "Marvellous Giegbefumwen",
						url = "https://github.com/gurumarv/sabiBank.git"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "sabiBank API documentation",
				url = "https://github.com/gurumarv/sabiBank.git"
		)
)
public class SabiBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(SabiBankApplication.class, args);
	}

}
