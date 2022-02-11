package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer;

public class BaseSparkRabbitMQProducerFactory extends SparkRabbitMQProducerFactory {

	private static final long serialVersionUID = 1L;

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String queue;
	private final String idSerializerClassName;
	private final String bodySerializerClassName; 

	public BaseSparkRabbitMQProducerFactory(
		String host, 
		int port, 
		String username, 
		String password, 
		String queue,
		String idSerializerClassName,
		String bodySerializerClassName
	) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.queue = queue;
		this.idSerializerClassName = idSerializerClassName;
		this.bodySerializerClassName = bodySerializerClassName;
	}

	@Override
	public <K, V> SparkRabbitMQProducer<K, V> newInstance() {
		return new SparkRabbitMQProducer<K, V>(host, port, username, password, queue, idSerializerClassName, bodySerializerClassName);
	}

}
