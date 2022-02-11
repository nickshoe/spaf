package it.unibo.disi.spaf.api.connectors.rabbitmq;

import it.unibo.disi.spaf.api.Sink;

public class RabbitMQSink<K, V> implements Sink<K, V> {

	private static final long serialVersionUID = 1L;

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String queue;
	private final String idSerializer;
	private final String bodySerializer;

	public RabbitMQSink(String host, int port, String username, String password, String queue, String idSerializer, String bodySerializer) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.queue = queue;
		this.idSerializer = idSerializer;
		this.bodySerializer = bodySerializer;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getQueue() {
		return queue;
	}

	public String getIdSerializer() {
		return idSerializer;
	}

	public String getBodySerializer() {
		return bodySerializer;
	}

	@Override
	public String getType() {
		return "rabbitmq";
	}

}
