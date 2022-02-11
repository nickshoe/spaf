package it.unibo.disi.spaf.api.connectors.rabbitmq;

import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.connectors.ConnectorsFactory;

public class RabbitMQConnectorsFactory extends ConnectorsFactory {

	private static RabbitMQConnectorsFactory instance;
	
	private RabbitMQConnectorsFactory() {}
	
	public static final RabbitMQConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new RabbitMQConnectorsFactory();
		}
		
		return instance;
	}
	
	@Override
	public <K, V> Source<K, V> createSource(Config config) {
		String host = config.getString("host");
		int port = config.getInt("port");
		String username = config.getString("username");
		String password = config.getString("password");
		String queue = config.getString("queue");
		String idDeserializer = config.getString("id-deserializer");
		String bodyDeserializer = config.getString("body-deserializer");
		
		return new RabbitMQSource<>(host, port, username, password, queue, idDeserializer, bodyDeserializer);
	}
	
	@Override
	public <K, V> Sink<K, V> createSink(Config config) {
		String host = config.getString("host");
		int port = config.getInt("port");
		String username = config.getString("username");
		String password = config.getString("password");
		String queue = config.getString("queue");
		String idSerializer = config.getString("id-serializer");
		String bodySerializer = config.getString("body-serializer");
		
		return new RabbitMQSink<>(host, port, username, password, queue, idSerializer, bodySerializer);
	}

}
