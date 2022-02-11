package it.unibo.disi.spaf.samza.connectors.kafka;

import java.util.Optional;

import org.apache.samza.application.descriptors.StreamApplicationDescriptor;
import org.apache.samza.application.descriptors.TaskApplicationDescriptor;
import org.apache.samza.operators.KV;
import org.apache.samza.operators.MessageStream;
import org.apache.samza.operators.OutputStream;
import org.apache.samza.serializers.KVSerde;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.kafka.descriptors.KafkaInputDescriptor;
import org.apache.samza.system.kafka.descriptors.KafkaOutputDescriptor;
import org.apache.samza.system.kafka.descriptors.KafkaSystemDescriptor;

import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSink;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSource;
import it.unibo.disi.spaf.samza.connectors.SamzaConnectorsFactory;
import it.unibo.disi.spaf.samza.connectors.kafka.utils.KafkaUtils;

public class KafkaSamzaConnectorsFactory extends SamzaConnectorsFactory {

	private static KafkaSamzaConnectorsFactory instance;
	
	private KafkaSystemDescriptor kafkaSystemDescriptor;
	
	private KafkaSamzaConnectorsFactory() {}
	
	public static final KafkaSamzaConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new KafkaSamzaConnectorsFactory();
		}
		
		return instance;
	}
	
	@Override
	public <K, V> void setupInputStream(TaskApplicationDescriptor appDescriptor, Source<?, ?> source) {
		KafkaInputDescriptor<KV<K, V>> inputDescriptor = buildKafkaInputDescriptor(source);

		appDescriptor.withInputStream(inputDescriptor);
	}
	
	@Override
	public <K, V> MessageStream<KV<K, V>> getInputStream(StreamApplicationDescriptor appDescriptor, Source<?, ?> source) {
		KafkaInputDescriptor<KV<K, V>> inputDescriptor = buildKafkaInputDescriptor(source);
		
		MessageStream<KV<K, V>> inputStream = appDescriptor.getInputStream(inputDescriptor);

		return inputStream;
	}

	@Override
	public <K, V> SystemStream setupOutputStream(TaskApplicationDescriptor appDescriptor, Sink<?, ?> sink) {
		KafkaOutputDescriptor<KV<K, V>> outputDescriptor = buildKafkaOutputDescriptor(sink);

		appDescriptor.withOutputStream(outputDescriptor);
		
		return new SystemStream(KafkaUtils.KAFKA_SYSTEM_NAME, outputDescriptor.getStreamId());
	}
	
	@Override
	public <K, V> OutputStream<KV<K, V>> getOutputStream(StreamApplicationDescriptor appDescriptor, Sink<?, ?> sink) {		
		KafkaOutputDescriptor<KV<K, V>> outputDescriptor = buildKafkaOutputDescriptor(sink);
		
		OutputStream<KV<K, V>> outputStream = appDescriptor.getOutputStream(outputDescriptor);

		return outputStream;
	}

	private <K, V> KafkaInputDescriptor<KV<K, V>> buildKafkaInputDescriptor(Source<?, ?> source) {
		KafkaSource<?, ?> kafkaSource = (KafkaSource<?, ?>) source;

		Optional<String> zookeeperConnect = kafkaSource.getZookeeperConnect();
		String bootstrapServers = kafkaSource.getBootstrapServers(); // TODO: make it a string list
		String topicName = kafkaSource.getTopicName();
		String keyDeserializerClassName = kafkaSource.getKeyDeserializer();
		String valueDeserializerClassName = kafkaSource.getValueDeserializer();

		initKafkaSystemDescriptor(zookeeperConnect, bootstrapServers);
		
		KVSerde<K, V> kvSerde = KafkaUtils.buildKVSerde(keyDeserializerClassName, valueDeserializerClassName);

		final String INPUT_STREAM_ID = topicName;
		KafkaInputDescriptor<KV<K, V>> inputDescriptor = this.kafkaSystemDescriptor.getInputDescriptor(INPUT_STREAM_ID, kvSerde);
		
		return inputDescriptor;
	}
	
	private <K, V> KafkaOutputDescriptor<KV<K, V>> buildKafkaOutputDescriptor(Sink<?, ?> sink) {
		KafkaSink<?, ?> kafkaSink = (KafkaSink<?, ?>) sink;
		
		String bootstrapServers = kafkaSink.getBootstrapServers(); // TODO: make it a string list
		String keySerializerClassName = kafkaSink.getKeySerializer();
		String valueSerializerClassName = kafkaSink.getValueSerializer();
		
		initKafkaSystemDescriptor(Optional.empty(), bootstrapServers);

		KVSerde<K, V> kvSerde = KafkaUtils.buildKVSerde(keySerializerClassName, valueSerializerClassName);
		
		final String OUTPUT_STREAM_ID = getOutputStreamId(kafkaSink);
		KafkaOutputDescriptor<KV<K, V>> outputDescriptor = this.kafkaSystemDescriptor.getOutputDescriptor(OUTPUT_STREAM_ID, kvSerde);
		
		return outputDescriptor;
	}
	
	private String getOutputStreamId(KafkaSink<?, ?> kafkaSink) {
		return kafkaSink.getTopicName();
	}

	private void initKafkaSystemDescriptor(Optional<String> zookeeperConnect, String bootstrapServers) {
		if (this.kafkaSystemDescriptor == null) {
			this.kafkaSystemDescriptor = KafkaUtils.buildKafkaSystemDescriptor(zookeeperConnect, bootstrapServers);	
		}
	}

}
