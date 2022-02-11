package it.unibo.disi.spaf.examples.utils.kafka;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.examples.utils.Image;
import it.unibo.disi.spaf.examples.utils.Utils;

public class KafkaImageSender {
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaImageSender.class);

	public static void main(String[] args) throws Exception {
		String bootstrapServer;
		String imagesDirPath;
		String topicName;
		
		if (args.length == 3) {
			bootstrapServer = args[0];
			imagesDirPath = args[1];
			topicName = args[2];
		} else {
			System.out.println("Usage: java -jar kafka-image-sender.jar <bootstrap server> <images dir> <topic name>");
			System.exit(1);
			
			return;
		}

		logger.info("Sending all images from dir {} to topic {}", imagesDirPath, topicName);
				
		List<Image> images = Utils.buildImagesList(imagesDirPath);

		KafkaProducer<String, byte[]> producer = buildKafkaProducer(bootstrapServer);

		for (Image image : images) {
			String imageFilename = image.getFilename();
			byte[] imageBytes = image.getBytes();
			
			ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(topicName, imageFilename, imageBytes);

			try {
				producer.send(producerRecord).get();
				
				logger.info("Image sent {} ({} bytes).", imageFilename, imageBytes.length);					
			} catch (Exception e) {
				logger.error("Image could not be sent {} ({} bytes): {}", imageFilename, imageBytes.length, e.getMessage());
			}
		}

		producer.close();
		
		System.exit(0);
	}
	
	private static KafkaProducer<String, byte[]> buildKafkaProducer(String bootstrapServer) {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", bootstrapServer);
		properties.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
		properties.put("value.serializer", org.apache.kafka.common.serialization.ByteArraySerializer.class);
		properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 33554432);

		KafkaProducer<String, byte[]> producer = new KafkaProducer<String, byte[]>(properties);

		return producer;
	}
}
