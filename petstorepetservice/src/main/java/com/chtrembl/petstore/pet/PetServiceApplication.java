package com.chtrembl.petstore.pet;

import com.chtrembl.petstore.pet.model.ContainerEnvironment;
import com.chtrembl.petstore.pet.model.DataPreload;
import com.microsoft.applicationinsights.attach.ApplicationInsights;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class PetServiceApplication implements CommandLineRunner {

	private static final String AI_CONNECTION_STRING_ENV = "APPLICATIONINSIGHTS_CONNECTION_STRING";
	private static final String APPLICATIONINSIGHTS_ENABLED = "APPLICATIONINSIGHTS_ENABLED";

	@Bean
	public ContainerEnvironment containerEnvvironment() {
		return new ContainerEnvironment();
	}

	@Bean
	public DataPreload dataPreload() {
		return new DataPreload();
	}

	@Override
	public void run(String... arg0) throws Exception {
		if (arg0.length > 0 && arg0[0].equals("exitcode")) {
			throw new ExitException();
		}
	}

	public static void main(String[] args) throws Exception {
		configureApplicationInsights();
        new SpringApplication(PetServiceApplication.class).run(args);
	}

	private static void configureApplicationInsights() {
		String aiEnabledStr = System.getenv(APPLICATIONINSIGHTS_ENABLED);
		boolean aiEnabled = !"false".equalsIgnoreCase(aiEnabledStr); // Default: true

		if (!aiEnabled) {
			log.info("Application Insights DISABLED via APPLICATIONINSIGHTS_ENABLED environment variable");
			return;
		}

		String connectionString = System.getenv(AI_CONNECTION_STRING_ENV);

		if (StringUtils.isNotBlank(connectionString)) {
			try {
				ApplicationInsights.attach();
				log.info("Application Insights enabled successfully");
			} catch (Exception e) {
				log.warn("Failed to attach Application Insights: {}", e.getMessage());
			}
		} else {
			log.info("Application Insights not configured (no connection string found). Please set the {} environment variable with correct connection string.", AI_CONNECTION_STRING_ENV);
		}
	}

	class ExitException extends RuntimeException implements ExitCodeGenerator {
		private static final long serialVersionUID = 1L;

		@Override
		public int getExitCode() {
			return 10;
		}

	}
}
