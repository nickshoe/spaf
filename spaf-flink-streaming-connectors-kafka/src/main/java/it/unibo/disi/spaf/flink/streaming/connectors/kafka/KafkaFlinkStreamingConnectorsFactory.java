package it.unibo.disi.spaf.flink.streaming.connectors.kafka;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSink;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSource;
import it.unibo.disi.spaf.flink.streaming.connectors.FlinkStreamingConnectorsFactory;
import it.unibo.disi.spaf.flink.streaming.connectors.kafka.serialization.ElementKafkaRecordDeserializationSchema;
import it.unibo.disi.spaf.flink.streaming.connectors.kafka.serialization.ElementKeySerializationSchema;
import it.unibo.disi.spaf.flink.streaming.connectors.kafka.serialization.ElementValueSerializationSchema;
import it.unibo.disi.spaf.internals.Element;

public class KafkaFlinkStreamingConnectorsFactory extends FlinkStreamingConnectorsFactory {

	private static KafkaFlinkStreamingConnectorsFactory instance;
	
	private KafkaFlinkStreamingConnectorsFactory() {}
	
	public static final KafkaFlinkStreamingConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new KafkaFlinkStreamingConnectorsFactory();
		}
		
		return instance;
	}
	
	@Override
	public DataStream<Element<Object, Object>> createInputStream(
		StreamExecutionEnvironment environment,
		Application application, 
		Source<?, ?> source
	) {
		KafkaSource<?, ?> kafkaSourceDescriptor = (KafkaSource<?, ?>) source;

		String bootstrapServers = kafkaSourceDescriptor.getBootstrapServers();
		String groupId = this.generateConsumerGroupId(application.getName());
		String topicName = kafkaSourceDescriptor.getTopicName();
		String keyDeserializerClassName = kafkaSourceDescriptor.getKeyDeserializer();
		String valueDeserializerClassName = kafkaSourceDescriptor.getValueDeserializer();

		KafkaRecordDeserializationSchema<Element<Object,Object>> deserializer = new ElementKafkaRecordDeserializationSchema(
			topicName,
			keyDeserializerClassName,
			valueDeserializerClassName
		);
		
		org.apache.flink.connector.kafka.source.KafkaSource<Element<Object, Object>> kafkaSource = org.apache.flink.connector.kafka.source.KafkaSource
				.<Element<Object, Object>>builder()
				.setBootstrapServers(bootstrapServers)
				.setGroupId(groupId)
				.setTopics(topicName)
				.setStartingOffsets(OffsetsInitializer.latest()) // TODO: make it configurable
				.setDeserializer(deserializer)
				.build();
		
		DataStreamSource<Element<Object, Object>> sourceStream = environment.fromSource(
			kafkaSource, 
			WatermarkStrategy.noWatermarks(), // TODO: what's this? 
			"source-name" // TODO: expose source name from API
		);
		
		return sourceStream;
	}

	private String generateConsumerGroupId(String appName) {
		return this.slugify(appName).concat("-consumers");
	}

	private String slugify(String string) {
		return String.join("-", string.trim().toLowerCase().split(" "));
	}

	@Override
	public void setupStreamOutput(DataStream<Element<Object, Object>> stream, Sink<?, ?> sink) {
		KafkaSink<?, ?> kafkaSink = (KafkaSink<?, ?>) sink;
		
		String topicName = kafkaSink.getTopicName();
		String keySerializerClassName = kafkaSink.getKeySerializer();
		String valueSerializerClassName = kafkaSink.getValueSerializer();
		
		SerializationSchema<Element<Object, Object>> keySerializationSchema = new ElementKeySerializationSchema(topicName, keySerializerClassName);
		SerializationSchema<Element<Object, Object>> valueSerializationSchema = new ElementValueSerializationSchema(topicName, valueSerializerClassName);
		
		org.apache.flink.connector.kafka.sink.KafkaSink<Element<Object, Object>> actualKafkaSink = org.apache.flink.connector.kafka.sink.KafkaSink
				.<Element<Object, Object>>builder()
				.setBootstrapServers(kafkaSink.getBootstrapServers())
				.setRecordSerializer(
					KafkaRecordSerializationSchema.<Element<Object, Object>>builder()
						.setTopic(kafkaSink.getTopicName())
						.setKeySerializationSchema(keySerializationSchema)
						.setValueSerializationSchema(valueSerializationSchema)
						.build()
				)
				.setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE) // TODO: from config
				.build();
		
		stream.sinkTo(actualKafkaSink);
	}

}
