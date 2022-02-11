package it.unibo.disi.spaf.storm.topology;

import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.ConfigurableTopology;
import org.apache.storm.topology.TopologyBuilder;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.internals.OneTimeInitProcessor;
import it.unibo.disi.spaf.storm.bolts.ProcessorBolt;
import it.unibo.disi.spaf.storm.connectors.StormConnectors;
import it.unibo.disi.spaf.storm.utils.LocalClusterWaiter;

public class EquivalentStormTopology extends ConfigurableTopology {

	private final Application application;
	private final Config externalConfig;

	public EquivalentStormTopology(Application application, Config config) {
		super();
		this.application = application;
		this.externalConfig = config;
	}

	@Override
	protected int run(String[] args) {
		// 1. Configuring and obtaining the execution context
		setupStormConfig();

		TopologyBuilder topologyBuilder = new TopologyBuilder();

		// 2. Mapping SPAF Source to Flink Source
		Source<?, ?> source = application.getTopology().getSource();
		String sourceName = "source-name"; // TODO: expose source name from API
		StormConnectors.setupSourceSpout(topologyBuilder, source, sourceName);

		// 3. Mapping SPAF Topology to Flink Streaming topology
		// TODO: abstract the way the SPAF topology gets "visited", so it will be easier to evolve to DAG topologies in the future (see Visitor pattern)
		String previousComponentId = sourceName;
		int processorId = 1;
		for (Processor<?, ?, ?, ?> processor : application.getTopology().getProcessors()) {
			// TODO: is there any way to avoid this unchecked cast?
			Processor<Object, Object, Object, Object> p = (Processor<Object, Object, Object, Object>) processor;
			Processor<Object, Object, Object, Object> processorWrapper = new OneTimeInitProcessor<>(p);

			ProcessorBolt processorBolt = new ProcessorBolt(processorWrapper);

			String boltId = "processor-" + processorId; // TODO: expose processor name from API
			topologyBuilder.setBolt(boltId, processorBolt).shuffleGrouping(previousComponentId);

			previousComponentId = boltId;

			processorId += 1;
		}

		// 4. Mapping SPAF Sink to Spark Streaming output
		Sink<?, ?> sink = application.getTopology().getSink();
		String sinkName = "sink-name"; // TODO: expose sink name from API
		StormConnectors.setupSinkBolt(topologyBuilder, sink, sinkName, previousComponentId);

		// 5. Application launch
		String topologyName = this.slugify(application.getName());
		return submitTopology(topologyName, topologyBuilder);
	}

	private String slugify(String string) {
		return String.join("-", string.trim().toLowerCase().split(" "));
	}

	private void setupStormConfig() {
		if (externalConfig.hasPath("storm.debug")) {
			boolean debug = externalConfig.getBoolean("storm.debug");
			conf.setDebug(debug);

			if (debug) {
				conf.setNumEventLoggers(1);
			}
		}

		int workers = externalConfig.getInt("storm.workers");
		conf.setNumWorkers(workers);

		// This is needed because we cannot yet register application specific DTO
		// classes on Kryo for serialization/deserialization
		conf.setFallBackOnJavaSerialization(true);
	}

	private int submitTopology(String topologyName, TopologyBuilder topologyBuilder) {
		boolean isLocalEnvironment = false;
		if (externalConfig.hasPath("storm.local")) {
			isLocalEnvironment = externalConfig.getBoolean("storm.local");
		}

		if (isLocalEnvironment) {
			return submitToLocalCluster(topologyName, topologyBuilder);
		} else {
			return submitToRemoteCluster(topologyName, topologyBuilder);
		}
	}

	// TODO: refactor
	private int submitToLocalCluster(String topologyName, TopologyBuilder topologyBuilder) {
		StormTopology stormTopology = topologyBuilder.createTopology();

		try (LocalCluster cluster = new LocalCluster()) {
			LocalClusterWaiter waiter = new LocalClusterWaiter();

			cluster.submitTopology(topologyName, conf, stormTopology);

			waiter.waitForStopOrError();
		} catch (Throwable e) {
			e.printStackTrace();

			return -1;
		}

		return 0;
	}

	private int submitToRemoteCluster(String topologyName, TopologyBuilder topologyBuilder) {
		return super.submit(topologyName, conf, topologyBuilder);
	}
	
}
