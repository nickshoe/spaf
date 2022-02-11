package it.unibo.disi.spaf.api;

import java.io.File;

public class ConfigFactory {
	
	public static final Config load() {
		com.typesafe.config.Config config = com.typesafe.config.ConfigFactory.load();
		
		// TODO: add config file example in the API package
		config = com.typesafe.config.ConfigFactory.parseFile(new File("./application.conf")).withFallback(config);
		
		return new Config(config);
	}
	
}
