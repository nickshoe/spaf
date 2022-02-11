package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.BodySerializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.IdSerializer;
import it.unibo.disi.spaf.common.utils.ReflectionUtils;

public class SparkRabbitMQProducer<K, V> {

	private final static Logger logger = LoggerFactory.getLogger(SparkRabbitMQProducer.class);
	
	private final String queue;
	
	private Connection connection;
	private Channel channel;
	
	private IdSerializer<K> idSerializer;
	private BodySerializer<V> bodySerializer;
	
	public SparkRabbitMQProducer(
		String host, 
		int port, 
		String username, 
		String password, 
		String queue, 
		String idSerializerClassName,
		String bodySerializerClassName
	) {
		super();

		this.queue = queue;
		
		setupConnection(host, port, username, password, queue, idSerializerClassName, bodySerializerClassName);
	}
	
	@SuppressWarnings("unchecked")
	private void setupConnection(
		String host, 
		int port, 
		String username, 
		String password, 
		String queue,
		String idSerializerClassName,
		String bodySerializerClassName
	) {
		ConnectionFactory factory = new ConnectionFactory();
		
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);		
		
		this.idSerializer = ReflectionUtils.getInstance(idSerializerClassName, IdSerializer.class);
		this.bodySerializer = ReflectionUtils.getInstance(bodySerializerClassName, BodySerializer.class);
		
		try {
			// the actual TCP connection to the broker
			this.connection = factory.newConnection();
			logger.debug("Connection established with the broker: {}", this.connection.getAddress().getHostName() + ":" + this.connection.getPort());
			
			// a channel can be thought of as "lightweight connections that share a single TCP connection"
			this.channel = this.connection.createChannel();
			logger.debug("Channel created over the existing connection: {}", this.channel.getChannelNumber());
			
			// TODO: durable: from config
			DeclareOk queueDeclaration = this.channel.queueDeclare(queue, false, false, false, null);
			logger.debug("Queue declared: {}", queueDeclaration.getQueue());
			
			logger.info("Producer ready to send messages to queue {}", queue);			
		} catch (IOException | TimeoutException e) {			
			logger.error("An error occurred while setting up the producer: {}", e.getMessage());
			
			throw new RuntimeException(e);
		}
	}

	public void send(K key, V value) {
		try {
			String messageId = this.idSerializer.serialize(key);
			byte[] messageBody = this.bodySerializer.serialize(value);
			
			AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().messageId(messageId).build();
			
			// TODO: should the exchange be topic-like? (otherwise round robin is being used...) - make it configurable?
			this.channel.basicPublish("", queue, properties, messageBody);
		} catch (IOException e) {
			logger.error("Could not publish message to queue {}: {}", queue, e.getMessage());
			
			throw new RuntimeException(e);
		}
	}
	
	public void close() {
		try {
			this.channel.close();	
			this.connection.close();
		} catch (IOException | TimeoutException e) {
			logger.error("Could not close channel: {}", e.getMessage());
			
			// TODO: should throw here?
		}
	}
}
