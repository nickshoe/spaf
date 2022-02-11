package it.unibo.disi.spaf.api.connectors.rabbitmq;

import it.unibo.disi.spaf.api.Source;

public class RabbitMQSource<K, V> implements Source<K, V> {

	private static final long serialVersionUID = 1L;

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String queue;
	private final String idDeserializer;
	private final String bodyDeserializer;

	public RabbitMQSource(String host, int port, String username, String password, String queue, String idDeserializer, String bodyDeserializer) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.queue = queue;
		this.idDeserializer = idDeserializer;
		this.bodyDeserializer = bodyDeserializer;
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

	public String getIdDeserializer() {
		return idDeserializer;
	}

	public String getBodyDeserializer() {
		return bodyDeserializer;
	}

	@Override
	public String getType() {
		return "rabbitmq";
	}

}
