package it.unibo.disi.spaf.api.connectors.kafka;

import java.util.Optional;

import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.connectors.ConnectorsFactory;

public class KafkaConnectorsFactory extends ConnectorsFactory {

	private static KafkaConnectorsFactory instance;
	
	private KafkaConnectorsFactory() {}
	
	public static final KafkaConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new KafkaConnectorsFactory();
		}
		
		return instance;
	}
	
	@Override
	public <K, V> Source<K, V> createSource(Config config) {
		String bootstrapServers = config.getString("bootstrap-servers"); // TODO: use a list
		String topic = config.getString("topic");
		String keyDeserializer = config.getString("key-deserializer");
		String valueDeserializer = config.getString("value-deserializer");
		// TODO: handle optional property for consumer-group

		KafkaSource<K, V> kafkaSource = new KafkaSource<>(bootstrapServers, topic, keyDeserializer, valueDeserializer);
		
		if (config.hasPath("zookeeper-connect")) {
			String zookeeperConnect = config.getString("zookeeper-connect");
			
			kafkaSource.setZookeeperConnect(Optional.ofNullable(zookeeperConnect));
		}	
		
		return kafkaSource;
	}
	
	@Override
	public <K, V> Sink<K, V> createSink(Config config) {
		String bootstrapServers = config.getString("bootstrap-servers"); // TODO: use a list
		String topic = config.getString("topic");
		String keySerializer = config.getString("key-serializer");
		String valueSerializer = config.getString("value-serializer");

		return new KafkaSink<>(bootstrapServers, topic, keySerializer, valueSerializer);
	}

}
