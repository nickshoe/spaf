package it.unibo.disi.spaf.samza.connectors.rabbitmq;

import org.apache.samza.application.descriptors.StreamApplicationDescriptor;
import org.apache.samza.application.descriptors.TaskApplicationDescriptor;
import org.apache.samza.operators.KV;
import org.apache.samza.operators.MessageStream;
import org.apache.samza.operators.OutputStream;
import org.apache.samza.serializers.KVSerde;
import org.apache.samza.system.SystemStream;

import io.github.nickshoe.samza.system.rabbitmq.descriptors.RabbitMQInputDescriptor;
import io.github.nickshoe.samza.system.rabbitmq.descriptors.RabbitMQOutputDescriptor;
import io.github.nickshoe.samza.system.rabbitmq.descriptors.RabbitMQSystemDescriptor;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSink;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSource;
import it.unibo.disi.spaf.samza.connectors.SamzaConnectorsFactory;
import it.unibo.disi.spaf.samza.connectors.rabbitmq.utils.RabbitMQUtils;

public class RabbitMQSamzaConnectorsFactory extends SamzaConnectorsFactory {

	private static RabbitMQSamzaConnectorsFactory instance;
	
	private RabbitMQSystemDescriptor rabbitMQSystemDescriptor;
	
	private RabbitMQSamzaConnectorsFactory() {}
	
	public static final RabbitMQSamzaConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new RabbitMQSamzaConnectorsFactory();
		}
		
		return instance;
	}
	
	@Override
	public <K, V> void setupInputStream(TaskApplicationDescriptor appDescriptor, Source<?, ?> source) {
		RabbitMQInputDescriptor<KV<K, V>> inputDescriptor = buildRabbitMQInputDescriptor(source);
		
		appDescriptor.withInputStream(inputDescriptor);
	}
	
	@Override
	public <K, V> MessageStream<KV<K, V>> getInputStream(StreamApplicationDescriptor appDescriptor, Source<?, ?> source) {
		RabbitMQInputDescriptor<KV<K, V>> inputDescriptor = buildRabbitMQInputDescriptor(source);
		
		MessageStream<KV<K, V>> inputStream = appDescriptor.getInputStream(inputDescriptor);

		return inputStream;
	}

	@Override
	public <K, V> SystemStream setupOutputStream(TaskApplicationDescriptor appDescriptor, Sink<?, ?> sink) {
		RabbitMQOutputDescriptor<KV<K, V>> outputDescriptor = buildRabbitMQOutputDescriptor(sink);
		
		appDescriptor.withOutputStream(outputDescriptor);
		
		return new SystemStream(RabbitMQUtils.RABBITMQ_SYSTEM_NAME, outputDescriptor.getStreamId());
	}
	
	@Override
	public <K, V> OutputStream<KV<K, V>> getOutputStream(StreamApplicationDescriptor appDescriptor, Sink<?, ?> sink) {
		RabbitMQOutputDescriptor<KV<K, V>> outputDescriptor = buildRabbitMQOutputDescriptor(sink);
		
		OutputStream<KV<K, V>> outputStream = appDescriptor.getOutputStream(outputDescriptor);

		return outputStream;
	}

	private <V, K> RabbitMQInputDescriptor<KV<K, V>> buildRabbitMQInputDescriptor(Source<?, ?> source) {
		RabbitMQSource<?, ?> rabbitMQSource = (RabbitMQSource<?, ?>) source;
		
		String host = rabbitMQSource.getHost();
		int port = rabbitMQSource.getPort();
		String username = rabbitMQSource.getUsername();
		String password = rabbitMQSource.getPassword();
		String queue = rabbitMQSource.getQueue();
		
		initRabbitMQSystemDescriptor(host, port, username, password);
		
		String idDeserializer = rabbitMQSource.getIdDeserializer();
		String bodyDeserializer = rabbitMQSource.getBodyDeserializer();
		
		KVSerde<K, V> kvSerde = RabbitMQUtils.buildKVSerde(idDeserializer, bodyDeserializer);
		
		final String INPUT_STREAM_ID = queue;
		RabbitMQInputDescriptor<KV<K, V>> inputDescriptor = this.rabbitMQSystemDescriptor.getInputDescriptor(INPUT_STREAM_ID, kvSerde);
		
		return inputDescriptor;
	}

	private <V, K> RabbitMQOutputDescriptor<KV<K, V>> buildRabbitMQOutputDescriptor(Sink<?, ?> sink) {
		RabbitMQSink<?, ?> rabbitMQSink = (RabbitMQSink<?, ?>) sink;
		
		String host = rabbitMQSink.getHost();
		int port = rabbitMQSink.getPort();
		String username = rabbitMQSink.getUsername();
		String password = rabbitMQSink.getPassword();
		
		initRabbitMQSystemDescriptor(host, port, username, password);
		
		String idSerializer = rabbitMQSink.getIdSerializer();
		String bodySerializer = rabbitMQSink.getBodySerializer();
		
		KVSerde<K, V> kvSerde = RabbitMQUtils.buildKVSerde(idSerializer, bodySerializer);
		
		final String OUTPUT_STREAM_ID = rabbitMQSink.getQueue();
		RabbitMQOutputDescriptor<KV<K, V>> outputDescriptor = this.rabbitMQSystemDescriptor.getOutputDescriptor(OUTPUT_STREAM_ID, kvSerde);
		
		return outputDescriptor;
	}
	
	private void initRabbitMQSystemDescriptor(String host, int port, String username, String password ) {
		if (rabbitMQSystemDescriptor == null) {
			rabbitMQSystemDescriptor = RabbitMQUtils.buildRabbitMQSystemDescriptor(host, port, username, password);
		}	
	}

}
