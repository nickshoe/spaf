package it.unibo.disi.spaf.samza.application.task;

import org.apache.samza.system.SystemStream;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.StreamTaskFactory;

import it.unibo.disi.spaf.api.Topology;

public class TopologyStreamTaskFactory implements StreamTaskFactory {

	private static final long serialVersionUID = 1L;

	private final Topology topology;
	private final SystemStream outputStream;

	public TopologyStreamTaskFactory(Topology topology,  SystemStream outputStream) {
		super();
		this.topology = topology;
		this.outputStream = outputStream;
	}

	@Override
	public StreamTask createInstance() {
		return new TopologyStreamTask(topology, outputStream);
	}

}
