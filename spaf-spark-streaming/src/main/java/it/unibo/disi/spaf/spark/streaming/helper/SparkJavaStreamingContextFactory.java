package it.unibo.disi.spaf.spark.streaming.helper;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Config;

public class SparkJavaStreamingContextFactory {
	
	private final static Logger logger = LoggerFactory.getLogger(SparkJavaStreamingContextFactory.class);
	
	private static SparkJavaStreamingContextFactory instance;
	
	private SparkJavaStreamingContextFactory() {}
	
	public static final SparkJavaStreamingContextFactory getInstance() {
		if (instance == null) {
			instance = new SparkJavaStreamingContextFactory();
		}
		
		return instance;
	}
	
	public JavaStreamingContext build(Config config, Application application) {
		SparkConf conf = new SparkConf().setAppName(application.getName());
		
		if (config.hasPath("spark.master")) {
			String sparkMaster = config.getString("spark.master");
			conf.setMaster(sparkMaster);	
		}
		
		Duration batchDuration;
		if (config.hasPath("spark.batch-duration-seconds")) {
			long seconds = config.getLong("spark.batch-duration-seconds");
			batchDuration = Durations.seconds(seconds);
		} else {
			logger.warn("Missing spark.batch-duration-seconds setting, using 1 second as default.");
			batchDuration = Durations.seconds(1);
		}
		
		return new JavaStreamingContext(conf, batchDuration);
	}
	
}
