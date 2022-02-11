package it.unibo.disi.spaf.storm.topology;

import org.apache.storm.topology.ConfigurableTopology;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Config;

public class EquivalentStormTopologyFactory {

	private static EquivalentStormTopologyFactory instance;
	
	private EquivalentStormTopologyFactory() {}
	
	public static final EquivalentStormTopologyFactory getInstance() {
		if (instance == null) {
			instance = new EquivalentStormTopologyFactory();
		}
		
		return instance;
	}
	
	public ConfigurableTopology buildEquivalentStormTopology(Application application, Config config) {
		EquivalentStormTopology stormTopology = new EquivalentStormTopology(application, config);
		
		return stormTopology;
	}
	
}
