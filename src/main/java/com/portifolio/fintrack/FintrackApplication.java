package com.portifolio.fintrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;

@SpringBootApplication
public class FintrackApplication {

	public static void main(String[] args) {
		configurarDatabaseUrlRender();
		SpringApplication.run(FintrackApplication.class, args);
	}

	private static void configurarDatabaseUrlRender() {
		String databaseUrl = System.getenv("DATABASE_URL");

		if (databaseUrl == null || databaseUrl.isBlank() || databaseUrl.startsWith("jdbc:")) {
			return;
		}

		if (!databaseUrl.startsWith("postgres://") && !databaseUrl.startsWith("postgresql://")) {
			return;
		}

		URI uri = URI.create(databaseUrl);
		String userInfo = uri.getUserInfo();
		String[] credentials = userInfo == null ? new String[0] : userInfo.split(":", 2);
		String query = uri.getQuery() == null ? "" : "?" + uri.getQuery();
		int port = uri.getPort() > 0 ? uri.getPort() : 5432;

		System.setProperty("spring.datasource.url", "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath() + query);

		if (credentials.length > 0 && !credentials[0].isBlank()) {
			System.setProperty("spring.datasource.username", credentials[0]);
		}

		if (credentials.length > 1 && !credentials[1].isBlank()) {
			System.setProperty("spring.datasource.password", credentials[1]);
		}
	}

}
