package it.unibo.disi.spaf.api.connectors.kafka;

import it.unibo.disi.spaf.api.Sink;

public class KafkaSink<K, V> implements Sink<K, V> {

	private static final long serialVersionUID = 1L;

	private final String bootstrapServers;
	private final String topicName;
	private final String keySerializer;
	private final String valueSerializer;

	public KafkaSink(String bootstrapServers, String topicName, String keySerializer, String valueSerializer) {
		super();
		this.bootstrapServers = bootstrapServers;
		this.topicName = topicName;
		this.keySerializer = keySerializer;
		this.valueSerializer = valueSerializer;
	}

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	public String getTopicName() {
		return topicName;
	}

	public String getKeySerializer() {
		return keySerializer;
	}

	public String getValueSerializer() {
		return valueSerializer;
	}

	@Override
	public String getType() {
		return "kafka";
	}
}
