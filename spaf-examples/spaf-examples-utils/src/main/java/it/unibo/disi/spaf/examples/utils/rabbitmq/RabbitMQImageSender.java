package it.unibo.disi.spaf.examples.utils.rabbitmq;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import it.unibo.disi.spaf.examples.utils.Image;
import it.unibo.disi.spaf.examples.utils.Utils;

public class RabbitMQImageSender {
	
	private static final Logger logger = LoggerFactory.getLogger(RabbitMQImageSender.class);
	
	public static void main(String[] args) {
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
			System.out.println("Usage: java -jar rabbitmq-image-sender.jar <host> <port> <username> <password> <queue name> <images dir>");
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
	    
	    logger.info("Sending all images from dir {} to queue {}", imagesDirPath, queueName);
		
		List<Image> images = Utils.buildImagesList(imagesDirPath);
	    
	    for (Image image : images) {
	    	String imageFilename = image.getFilename();
			byte[] imageBytes = image.getBytes();
			
			try {
				AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().messageId(imageFilename).build();
				
				// TODO: should the exchange be topic-like? (otherwise round robin is being used...)
				channel.basicPublish("", queueName, properties, imageBytes);
				
				logger.info("Image sent {} ({} bytes).", imageFilename, imageBytes.length);					
			} catch (Exception e) {
				logger.error("Image could not be sent {} ({} bytes): {}", imageFilename, imageBytes.length, e.getMessage());
			}
		}
	    
	    System.exit(0);
	}
	
}
