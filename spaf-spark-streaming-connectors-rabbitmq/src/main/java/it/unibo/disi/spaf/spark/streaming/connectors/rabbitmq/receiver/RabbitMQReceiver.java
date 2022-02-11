package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.receiver;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;

import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.BodyDeserializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.IdDeserializer;
import it.unibo.disi.spaf.common.utils.ReflectionUtils;
import it.unibo.disi.spaf.internals.Element;

public class RabbitMQReceiver<K, V> extends Receiver<Element<K, V>> {

	private final static Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);
	
	private static final long serialVersionUID = 1L;

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String queueName;
	private final boolean shouldDeclareQueue;
	private final String idDeserializerClassName;
	private final String bodyDeserializerClassName;
	private final boolean shouldAutoAck;
	
	private Connection connection;
	private Channel channel;

	private Thread thread;

	public RabbitMQReceiver(
		String host, 
		int port, 
		String username, 
		String password, 
		String queueName, 
		StorageLevel storageLevel,
		String idDeserializerClassName, 
		String bodyDeserializerClassName
	) {
		super(storageLevel);
		
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.queueName = queueName;
		this.shouldDeclareQueue = false; // TODO: from configs
		this.idDeserializerClassName = idDeserializerClassName;
		this.bodyDeserializerClassName = bodyDeserializerClassName;
		this.shouldAutoAck = true; // TODO: from configs
	}

	@Override
	public void onStart() {
		logger.info("RabbitMQ receiver starting");
		
		this.thread = new Thread(this::receive);
		
		this.thread.start();
	}

	@Override
	public void onStop() {
		if (this.channel != null) {
			try {
				this.channel.close();	
			} catch (IOException | TimeoutException e) {
				logger.error("Could not close channel: {}", e.getMessage());
				e.printStackTrace(); // TODO: should rethrow here?
			}
		}
		
		if (this.connection != null) {			
			try {
				this.connection.close();
			} catch (IOException e) {
				logger.error("Could not close connection to broker: {}", e.getMessage());
				e.printStackTrace(); // TODO: should rethrow here?
			}
		}
		
		this.thread.interrupt();
		
		logger.info("RabbitMQ receiver stopped");
	}
	
	@SuppressWarnings("unchecked")
	private void receive() {
		ConnectionFactory factory = new ConnectionFactory();
		
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
		
		IdDeserializer<K> idDeserializer = ReflectionUtils.getInstance(idDeserializerClassName, IdDeserializer.class);
		BodyDeserializer<V> bodyDeserializer = ReflectionUtils.getInstance(bodyDeserializerClassName, BodyDeserializer.class);
		
		try {
			// the actual TCP connection to the broker
			this.connection = factory.newConnection();
			logger.debug("Connection established with the broker: {}", this.connection.getAddress().getHostName() + ":" + this.connection.getPort());
			
			// a channel can be thought of as "lightweight connections that share a single TCP connection"
			this.channel = this.connection.createChannel();
			logger.debug("Channel created over the existing connection: {}", this.channel.getChannelNumber());
			
			if (this.shouldDeclareQueue) {
				performQueueDeclaration();
			}
			
			logger.info("Start consuming data from queue {}", queueName);
			this.channel.basicConsume(
				queueName,
				this.shouldAutoAck,
				(String consumerTag, Delivery message) -> {
					byte[] messageBody = message.getBody();
					
					String messageId = message.getProperties().getMessageId();
					
					// Transform RabbitMQ message into a SPAF element
					K key = idDeserializer.deserialize(messageId);
					V value = bodyDeserializer.deserialize(messageBody);
					Element<K, V> element = new Element<K, V>(key, value);
					
					super.store(element);
				},
				(String consumerTag) -> {
					logger.error("The message consumer {} was cancelled...", consumerTag);
					
					super.restart("The message consumer was cancelled");
				}
			);
		} catch (IOException | TimeoutException e) {			
			logger.error("An error occurred while setting up the consumer: {}", e.getMessage());

			e.printStackTrace();
			
			// TODO: restart? (see: https://spark.apache.org/docs/latest/streaming-custom-receivers.html)
		}
	}

	private void performQueueDeclaration() throws IOException {
		boolean durable = false; // TODO: from configs
		boolean exclusive = false;
		boolean autoDelete = false;
		Map<String, Object> arguments = null;
		
		DeclareOk queueDeclaration = this.channel.queueDeclare(this.queueName, durable, exclusive, autoDelete, arguments);
		
		logger.debug("Queue declared: {}", queueDeclaration.getQueue());
	}
	
}
