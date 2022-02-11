package it.unibo.disi.spaf.api.connectors.kafka;

import java.util.Optional;

import it.unibo.disi.spaf.api.Source;

public class KafkaSource<K, V> implements Source<K, V> {

	private static final long serialVersionUID = 1L;

	private final String bootstrapServers; // TODO: support list of bootstrap servers
	private final String topicName;
	private final String keyDeserializer;
	private final String valueDeserializer;
	private Optional<String> zookeeperConnect = Optional.empty();

	public KafkaSource(String bootstrapServers, String topicName, String keyDeserializer, String valueDeserializer) {
		super();
		this.bootstrapServers = bootstrapServers;
		this.topicName = topicName;
		this.keyDeserializer = keyDeserializer;
		this.valueDeserializer = valueDeserializer;
	}

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	public String getTopicName() {
		return topicName;
	}

	public String getKeyDeserializer() {
		return keyDeserializer;
	}

	public String getValueDeserializer() {
		return valueDeserializer;
	}

	public Optional<String> getZookeeperConnect() {
		return zookeeperConnect;
	}

	public void setZookeeperConnect(Optional<String> zookeeperConnect) {
		this.zookeeperConnect = zookeeperConnect;
	}

	@Override
	public String getType() {
		return "kafka";
	}

}
