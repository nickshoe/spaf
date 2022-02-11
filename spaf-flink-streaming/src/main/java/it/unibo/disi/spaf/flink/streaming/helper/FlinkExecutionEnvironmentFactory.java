package it.unibo.disi.spaf.flink.streaming.helper;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.Config;

public class FlinkExecutionEnvironmentFactory {
	
	private final static Logger logger = LoggerFactory.getLogger(FlinkExecutionEnvironmentFactory.class);

	private static FlinkExecutionEnvironmentFactory instance;
	
	private FlinkExecutionEnvironmentFactory() {
		super();
	}

	public static final FlinkExecutionEnvironmentFactory getInstance() {
		if (instance == null) {
			instance = new FlinkExecutionEnvironmentFactory();
		}
		
		return instance;
	}

	public StreamExecutionEnvironment build(Config config) {
		StreamExecutionEnvironment env;
		
		boolean isLocalEnvironment = config.getBoolean("flink.local");
		if (isLocalEnvironment) {
			logger.debug("Creating a local stream execution environment...");
			
			boolean withWebUI = config.getBoolean("flink.web-ui");
			if (withWebUI) {
				int webUIPort = config.getInt("flink.web-ui-port");
				Configuration envConfiguration = new Configuration().set(RestOptions.PORT, webUIPort);
				env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(envConfiguration);
			} else {
				env = StreamExecutionEnvironment.createLocalEnvironment();
			}	
		} else {
			logger.debug("Getting the stream execution environment...");
			
			env = StreamExecutionEnvironment.getExecutionEnvironment();
		}
		
		return env;
	}
	
}
