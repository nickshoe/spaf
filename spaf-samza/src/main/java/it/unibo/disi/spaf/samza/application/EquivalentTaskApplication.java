package it.unibo.disi.spaf.samza.application;

import org.apache.samza.application.TaskApplication;
import org.apache.samza.application.descriptors.TaskApplicationDescriptor;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.StreamTaskFactory;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.samza.application.task.TopologyStreamTaskFactory;
import it.unibo.disi.spaf.samza.connectors.SamzaConnectors;

public class EquivalentTaskApplication implements TaskApplication {

	private final Application application;

	public EquivalentTaskApplication(Application application) {
		super();
		this.application = application;
	}

	@Override
	public void describe(TaskApplicationDescriptor appDescriptor) {
		// 2. Mapping SPAF Source to Samza input stream
		SamzaConnectors.setupInputStream(
			appDescriptor,
			this.application.getTopology().getSource()
		);
		
		// 4. Mapping SPAF Sink to Samza output stream
		SystemStream outputStream = SamzaConnectors.setupOutputStream(
			appDescriptor, 
			this.application.getTopology().getSink()
		);
		
		// 3. Mapping SPAF Topology to Samza topology		
		StreamTaskFactory topologyStreamTaskFactory = new TopologyStreamTaskFactory(this.application.getTopology(), outputStream);
		appDescriptor.withTaskFactory(topologyStreamTaskFactory);
	}

}
