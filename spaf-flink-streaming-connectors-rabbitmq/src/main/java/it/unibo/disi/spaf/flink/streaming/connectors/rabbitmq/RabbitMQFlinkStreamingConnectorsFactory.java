package it.unibo.disi.spaf.flink.streaming.connectors.rabbitmq;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSink;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSource;
import it.unibo.disi.spaf.flink.streaming.connectors.FlinkStreamingConnectorsFactory;
import it.unibo.disi.spaf.flink.streaming.connectors.rabbitmq.serialization.ElementRMQSinkPublishOptions;
import it.unibo.disi.spaf.flink.streaming.connectors.rabbitmq.serialization.ElementRabbitMQDeserializationSchema;
import it.unibo.disi.spaf.flink.streaming.connectors.rabbitmq.serialization.ElementValueSerializationSchema;
import it.unibo.disi.spaf.internals.Element;

public class RabbitMQFlinkStreamingConnectorsFactory extends FlinkStreamingConnectorsFactory {

	private static RabbitMQFlinkStreamingConnectorsFactory instance;
	
	private RabbitMQFlinkStreamingConnectorsFactory() {}
	
	public static final RabbitMQFlinkStreamingConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new RabbitMQFlinkStreamingConnectorsFactory();
		}
		
		return instance;
	}
	
	@Override
	public DataStream<Element<Object, Object>> createInputStream(
		StreamExecutionEnvironment environment,
		Application application, 
		Source<?, ?> source
	) {
		RabbitMQSource<?, ?> rabbitMQSourceDescriptor = (RabbitMQSource<?, ?>) source;
		
		String host = rabbitMQSourceDescriptor.getHost();
		int port = rabbitMQSourceDescriptor.getPort();
		String username = rabbitMQSourceDescriptor.getUsername();
		String password = rabbitMQSourceDescriptor.getPassword();
		String queue = rabbitMQSourceDescriptor.getQueue();
		String idDeserializer = rabbitMQSourceDescriptor.getIdDeserializer();
		String bodyDeserializer = rabbitMQSourceDescriptor.getBodyDeserializer();

		final RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
				.setHost(host)
				.setPort(port)
				.setUserName(username)
				.setPassword(password)
				.setVirtualHost("/") // TODO: make it configurable?
				.build();
		
		final ElementRabbitMQDeserializationSchema deserializationSchema = new ElementRabbitMQDeserializationSchema(idDeserializer, bodyDeserializer);
		boolean usesCorrelationId = false;
		final RMQSource<Element<Object, Object>> rabbitMQSource = new RMQSource<Element<Object, Object>>(
			connectionConfig, 
			queue, 
			usesCorrelationId, 
			deserializationSchema
		); 

		final DataStreamSource<Element<Object, Object>> sourceStream = environment.addSource(
			rabbitMQSource,
			"source-name" // TODO: expose source name from API
		);
		
		return sourceStream;
	}

	@Override
	public void setupStreamOutput(DataStream<Element<Object, Object>> stream, Sink<?, ?> sink) {
		RabbitMQSink<?, ?> rabbitMQSinkDescriptor = (RabbitMQSink<?, ?>) sink;
		
		String host = rabbitMQSinkDescriptor.getHost();
		int port = rabbitMQSinkDescriptor.getPort();
		String username = rabbitMQSinkDescriptor.getUsername();
		String password = rabbitMQSinkDescriptor.getPassword();
		String queue = rabbitMQSinkDescriptor.getQueue();
		String idSerializerClassName = rabbitMQSinkDescriptor.getIdSerializer();
		String bodySerializerClassName = rabbitMQSinkDescriptor.getBodySerializer();
		
		final RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
				.setHost(host)
				.setPort(port)
				.setUserName(username)
				.setPassword(password)
				.setVirtualHost("/") // TODO: make it configurable?
				.build();
		
		ElementValueSerializationSchema serializationSchema = new ElementValueSerializationSchema(bodySerializerClassName);
		ElementRMQSinkPublishOptions publishOptions = new ElementRMQSinkPublishOptions(queue, idSerializerClassName);
		final RMQSink<Element<Object, Object>> rabbitMQSink = new RMQSink<Element<Object, Object>>(
			    connectionConfig,
			    serializationSchema,
			    publishOptions
		);
		
		stream.addSink(rabbitMQSink);
	}

}
