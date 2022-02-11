package it.unibo.disi.spaf.examples.alpr.processors;

import com.openalpr.jni.Alpr;
import com.openalpr.jni.AlprException;
import com.openalpr.jni.AlprResults;
import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Processor;

public class AlprProcessor implements Processor<String, byte[], String, AlprResults> {

	private static final long serialVersionUID = 1L;

	private final Config config;
	
	private Alpr alpr;
	
	public AlprProcessor(Config config) {
		super();
		this.config = config;
	}
	
	@Override
	public void init() {
		Config alprConfig = this.config.getSubConfig("application.alpr");
		
		this.alpr = new Alpr(
			alprConfig.getString("country"), 
			alprConfig.getString("configFile"),
			alprConfig.getString("runtimeDataDir")
		);
	}
	
	@Override
	public void process(String imageName, byte[] imageBytes, Collector<String, AlprResults> collector) {
		try {
			this.alpr.setTopN(1);

			// TODO: how to log in app processor using slf4j?
			System.out.println("[ALPR] analyzing image: " + imageName);

			AlprResults	alprResults = this.alpr.recognize(imageBytes);

			collector.collect(imageName, alprResults);
		} catch (AlprException e) {
			System.err.println("[ALPR] something went wrong analyzing image: " + imageName);

			e.printStackTrace();
		}
	}

}
