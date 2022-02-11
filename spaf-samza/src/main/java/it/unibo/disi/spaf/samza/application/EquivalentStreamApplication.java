package it.unibo.disi.spaf.samza.application;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.samza.application.StreamApplication;
import org.apache.samza.application.descriptors.StreamApplicationDescriptor;
import org.apache.samza.operators.KV;
import org.apache.samza.operators.MessageStream;
import org.apache.samza.operators.OutputStream;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.internals.CollectorImpl;
import it.unibo.disi.spaf.internals.OneTimeInitProcessor;
import it.unibo.disi.spaf.samza.connectors.SamzaConnectors;

public class EquivalentStreamApplication implements StreamApplication {

	private final Application application;

	public EquivalentStreamApplication(Application application) {
		super();
		this.application = application;
	}

	@Override
	public void describe(StreamApplicationDescriptor appDescriptor) {

		// 2. Mapping SPAF Source to Samza input stream
		MessageStream<KV<Object, Object>> stream = SamzaConnectors.getInputStream(
			appDescriptor,
			this.application.getTopology().getSource()
		);
		
		// 3. Mapping SPAF Topology to Samza topology
		// TODO: abstract the way the SPAF topology gets "visited", so it will be easier to evolve to DAG topologies in the future (see Visitor pattern)
		for (Processor<?, ?, ?, ?> processor : application.getTopology().getProcessors()) {
			// TODO: is there any way to avoid this unchecked cast?
			Processor<Object, Object, Object, Object> p = (Processor<Object, Object, Object, Object>) processor;
			Processor<Object, Object, Object, Object> processorWrapper = new OneTimeInitProcessor<>(p);
			
			stream = stream.flatMap(message -> {
				processorWrapper.init();
				
				Collector<Object, Object> collector = new CollectorImpl<>();
				
				processorWrapper.process(message.getKey(), message.getValue(), collector);

				List<KV<Object, Object>> outputMessages = StreamSupport.stream(collector.spliterator(), false)
					.map(element -> new KV<>(element.getKey(),element.getValue()))
					.collect(Collectors.toList());

				return outputMessages;
			});
		}
		
		// 4. Mapping SPAF Sink to Samza output stream
		OutputStream<KV<Object, Object>> outputStream = SamzaConnectors.getOutputStream(
			appDescriptor, 
			this.application.getTopology().getSink()
		);
		
		stream.sendTo(outputStream);
	}

}
