package it.unibo.disi.spaf.storm;

import org.apache.storm.topology.ConfigurableTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.storm.topology.EquivalentStormTopologyFactory;

public class StormContext implements Context {

	private final static Logger logger = LoggerFactory.getLogger(StormContext.class);

	private final Config config;

	public StormContext(Config config) {
		super();
		this.config = config;
	}

	@Override
	public void run(Application application) {
		logger.info("Running application {}", application.getName());

		ConfigurableTopology stormTopology = EquivalentStormTopologyFactory.getInstance().buildEquivalentStormTopology(application, config);
		
		String[] startArgs = {};
		if (this.config.hasPath("storm.start-args")) {
			startArgs =  this.config.getStringList("storm.start-args").toArray(new String[0]);	
		}

		ConfigurableTopology.start(stormTopology, startArgs);

		logger.info("Starting the topology...", application.getName());
	}

}
