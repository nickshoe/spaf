package it.unibo.disi.spaf.storm.connectors.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.spout.FirstPollOffsetStrategy;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutConfig.ProcessingGuarantee;
import org.apache.storm.topology.TopologyBuilder;

import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSink;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSource;
import it.unibo.disi.spaf.storm.connectors.StormConnectorsFactory;
import it.unibo.disi.spaf.storm.connectors.kafka.bolt.ElementTupleToKafkaMapper;
import it.unibo.disi.spaf.storm.connectors.kafka.spout.ElementRecordTranslator;

public class KafkaStormConnectorsFactory extends StormConnectorsFactory {

	private static KafkaStormConnectorsFactory instance;
	
	private KafkaStormConnectorsFactory() {}
	
	public static KafkaStormConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new KafkaStormConnectorsFactory();
		}
		
		return instance;
	}
	
	@Override
	public void setupSourceSpout(TopologyBuilder topologyBuilder, Source<?, ?> source, String sourceName) {
		KafkaSource<?, ?> kafkaSource = (KafkaSource<?, ?>) source;
		
		String boostrapServers = kafkaSource.getBootstrapServers();
		String inputTopicName = kafkaSource.getTopicName();
		String keyDeserializer = kafkaSource.getKeyDeserializer();
		String valueDeserializer = kafkaSource.getValueDeserializer();
		
		KafkaSpoutConfig.Builder<Object, Object> builder = new KafkaSpoutConfig.Builder<Object, Object>(boostrapServers, inputTopicName)
				.setFirstPollOffsetStrategy(FirstPollOffsetStrategy.LATEST) // TODO: from config
				.setProcessingGuarantee(ProcessingGuarantee.AT_LEAST_ONCE) // TODO: from config
				.setRecordTranslator(new ElementRecordTranslator())
				.setProp(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer)
				.setProp(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
		KafkaSpoutConfig<?, ?> kafkaSpoutConfig = builder.build();
		
		KafkaSpout<?, ?> kafkaSpout = new KafkaSpout<>(kafkaSpoutConfig);

		topologyBuilder.setSpout(sourceName, kafkaSpout);
	}

	@Override
	public void setupSinkBolt(TopologyBuilder topologyBuilder, Sink<?, ?> sink, String sinkName, String previousComponentId) {
		KafkaSink<?, ?> kafkaSink = (KafkaSink<?, ?>) sink;
		
		String boostrapServers = kafkaSink.getBootstrapServers();
		String outputTopicName = kafkaSink.getTopicName();
		String keySerializer = kafkaSink.getKeySerializer();
		String valueSerializer = kafkaSink.getValueSerializer();
		
		Properties kafkaProducerProps = new Properties();
		kafkaProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
		kafkaProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
		kafkaProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
		kafkaProducerProps.put(ProducerConfig.ACKS_CONFIG, "all"); // TODO: make it configurable?
		kafkaProducerProps.put(ProducerConfig.RETRIES_CONFIG, 0); // TODO: make it configurable?
		kafkaProducerProps.put(ProducerConfig.LINGER_MS_CONFIG, 1); // TODO: make it configurable?
		
		KafkaBolt<?, ?> kafkaBolt = new KafkaBolt<>()
			.withProducerProperties(kafkaProducerProps)
			.withTopicSelector(outputTopicName)
			.withTupleToKafkaMapper(new ElementTupleToKafkaMapper());

		topologyBuilder.setBolt(sinkName, kafkaBolt).shuffleGrouping(previousComponentId);
	}
	
}
