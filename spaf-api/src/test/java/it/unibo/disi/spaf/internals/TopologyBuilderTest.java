package it.unibo.disi.spaf.internals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.Topology;
import it.unibo.disi.spaf.api.connectors.MockSink;
import it.unibo.disi.spaf.api.connectors.MockSource;
import it.unibo.disi.spaf.common.exceptions.TopologyException;

public class TopologyBuilderTest {
	
	@Test()
	public void testTwoProcessorCannotHaveTheSameSourceAsPredecessor() {
		Assertions.assertThrows(TopologyException.class, () -> {
			Source<?, ?> source = new MockSource<>();
			
			new Topology()
				.setSource("TheSource", source)
				.addProcessor("ProcessorA", (Object key, Object value, Collector<Object, Object> collector) -> {}, "TheSource")
				.addProcessor("ProcessorB", (Object key, Object value, Collector<Object, Object> collector) -> {}, "TheSource");
		});
	}
	
	@Test()
	public void testCannotSetTwoSink() {
		Assertions.assertThrows(TopologyException.class, () -> {
			Sink<?, ?> sinkA = new MockSink<>();
			Sink<?, ?> sinkB = new MockSink<>();
			
			new Topology()
				.setSink("SinkA", sinkA)
				.setSink("SinkB", sinkB);
		});
	}
}
