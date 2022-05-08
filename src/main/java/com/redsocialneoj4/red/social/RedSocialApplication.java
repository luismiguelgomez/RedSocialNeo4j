package com.redsocialneoj4.red.social;

import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@SpringBootApplication
public class RedSocialApplication {

	public static void main(String[] args) throws Exception {
		String uri = "neo4j+s://d66f24f0.databases.neo4j.io";

		String user = "";
		String password = "";
		try (AppTest app = new AppTest(uri, user, password, Config.defaultConfig())) {
			app.createSeller("Alice", "1020304050");
			app.findPerson("Alice");
		}
		SpringApplication.run(RedSocialApplication.class, args);
	}
}
