package it.unibo.disi.spaf.samza.application.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.samza.context.Context;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;

import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.api.Topology;
import it.unibo.disi.spaf.internals.CollectorImpl;
import it.unibo.disi.spaf.internals.Element;
import it.unibo.disi.spaf.internals.OneTimeInitProcessor;

public class TopologyStreamTask implements StreamTask, InitableTask {

	private final List<Processor<Object, Object, Object, Object>> processors;
	private final SystemStream outputStream;

	public TopologyStreamTask(Topology topology, SystemStream outputStream) {
		super();
		
		processors = new ArrayList<>();
		
		// TODO: abstract the way the SPAF topology gets "visited", so it will be easier to evolve to DAG topologies in the future (see Visitor pattern)
		for (Processor<?, ?, ?, ?> processor : topology.getProcessors()) {
			// TODO: is there any way to avoid this unchecked cast?
			Processor<Object, Object, Object, Object> p = (Processor<Object, Object, Object, Object>) processor;
			Processor<Object, Object, Object, Object> processorWrapper = new OneTimeInitProcessor<>(p);
			
			processors.add(processorWrapper);
		}
		
		this.outputStream = outputStream;
	}

	@Override
	public void init(Context context) {
		processors.forEach(Processor::init);
	}

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
		Object key = envelope.getKey();
		Object value = envelope.getMessage();
		
		Element<Object, Object> inputElement = new Element<>(key, value);
		
		List<Element<Object, Object>> elementsToProcess = new ArrayList<>();
		elementsToProcess.add(inputElement);
		
		// TODO: abstract the way the SPAF topology gets "visited", so it will be easier to evolve to DAG topologies in the future (see Visitor pattern)
		for (Processor<Object, Object, Object, Object> processor : processors) {
			
			Collector<Object, Object> processedElementsCollector = new CollectorImpl<>();
			
			for (Element<Object, Object> elementToProcess : elementsToProcess) {
				processor.process(elementToProcess.getKey(), elementToProcess.getValue(), processedElementsCollector);	
			}

			List<Element<Object, Object>> processedElements = StreamSupport
				.stream(processedElementsCollector.spliterator(), false)
				.collect(Collectors.toList());

			elementsToProcess = processedElements;
		}
		
		List<Element<Object, Object>> outputElements = elementsToProcess;
		
		for (Element<Object, Object> processedElement : outputElements) {
			OutgoingMessageEnvelope outEnvelope = new OutgoingMessageEnvelope(outputStream, processedElement.getKey(), processedElement.getValue());
			
			collector.send(outEnvelope);
		}
	}

}
