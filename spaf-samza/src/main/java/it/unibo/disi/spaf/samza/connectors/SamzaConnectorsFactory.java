package it.unibo.disi.spaf.samza.connectors;

import org.apache.samza.application.descriptors.StreamApplicationDescriptor;
import org.apache.samza.application.descriptors.TaskApplicationDescriptor;
import org.apache.samza.operators.KV;
import org.apache.samza.operators.MessageStream;
import org.apache.samza.operators.OutputStream;
import org.apache.samza.system.SystemStream;

import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.samza.exceptions.SamzaStreamProcessingException;

public abstract class SamzaConnectorsFactory {
	
	public abstract <K, V> void setupInputStream(TaskApplicationDescriptor appDescriptor, Source<?, ?> source);
	
	public <K, V> MessageStream<KV<K, V>> getInputStream(StreamApplicationDescriptor appDescriptor, Source<?, ?> source) {
		throw new SamzaStreamProcessingException("This method is not implemented by the subclass");
	}
	
	public abstract <K, V> SystemStream setupOutputStream(TaskApplicationDescriptor appDescriptor, Sink<?, ?> sink);
	
	public <K, V> OutputStream<KV<K, V>> getOutputStream(StreamApplicationDescriptor appDescriptor, Sink<?, ?> sink) {
		throw new SamzaStreamProcessingException("This method is not implemented by the subclass");
	}
	
}
