package it.unibo.disi.spaf.examples.utils.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.examples.utils.Utils;

public class KafkaImageReceiver {

	private static final Logger logger = LoggerFactory.getLogger(KafkaImageReceiver.class);
	
	public static void main(String[] args) throws Exception {
		String bootstrapServer;
		String imagesDirPath;
		String topicName;
				
		if (args.length == 3) {
			bootstrapServer = args[0];
			imagesDirPath = args[1];
			topicName = args[2];
		} else {
			System.out.println("Usage: java -jar kafka-image-receiver.jar <bootstrap server> <images dir> <topic name>");
			System.exit(1);
			
			return;
		}
		
		Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServer);
        properties.put("group.id", "kafka-receiver-consumer-group");
        properties.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
        properties.put("value.deserializer", org.apache.kafka.common.serialization.ByteArrayDeserializer.class);
        properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 33554432);
        
        @SuppressWarnings("resource")
		KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<String, byte[]>(properties);
        
        consumer.subscribe(Arrays.asList(topicName));
        
        while (true) {
        	ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
        	
            for (ConsumerRecord<String, byte[]> record : records) {            
            	String imageFilename = record.key();
            	byte[] imageBytes = record.value();
            	
            	logger.info("Image received " + imageFilename + " " + imageBytes.length  + " bytes");
            	
				Utils.saveImage(imageFilename, imageBytes, imagesDirPath);
            }
        }
	}
}
