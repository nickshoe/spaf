package it.unibo.disi.spaf.examples.utils.rabbitmq;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import it.unibo.disi.spaf.examples.utils.Utils;

public class RabbitMQImageReceiver {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMQImageReceiver.class);

	public static void main(String[] args) throws Exception {

		String host;
		int port;
		String username;
		String password;
		String queueName;
		String imagesDirPath;

		if (args.length == 6) {
			host = args[0];
			port = Integer.parseInt(args[1]);
			username = args[2];
			password = args[3];
			queueName = args[4];
			imagesDirPath = args[5];
		} else {
			System.out.println("Usage: java -jar rabbitmq-image-receiver.jar <host> <port> <username> <password> <queue name> <images dir>");
			System.exit(1);

			return;
		}

		ConnectionFactory factory = new ConnectionFactory();

		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);

		Connection connection;
		try {
			connection = factory.newConnection();
		} catch (Exception e) {
			logger.error("Could not create connection: {}", e.getMessage());
			System.exit(1);

			return;
		}

		Channel channel;
		try {
			channel = connection.createChannel();
		} catch (Exception e) {
			logger.error("Could not create channel: {}", e.getMessage());
			System.exit(1);

			return;
		}

		// TODO: introduce a param
		/*
		try {
			channel.queueDeclare(queueName, false, false, false, null);
		} catch (Exception e) {
			logger.error("Could not declare channel {}: {}", queueName, e.getMessage());
			System.exit(1);

			return;
		}
		*/

		System.out.println("Waiting for files. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
			
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String imageFilename = properties.getMessageId();
				byte[] imageBytes = body;

				logger.info("Image received " + imageFilename + " " + imageBytes.length + " bytes");

				Utils.saveImage(imageFilename, imageBytes, imagesDirPath);
			}
			
			@Override
			public void handleCancel(String consumerTag) throws IOException {
				logger.warn("Consumer cancelled {}", consumerTag);
			}
			
		};

		channel.basicConsume(queueName, true, consumer);
	}
}
