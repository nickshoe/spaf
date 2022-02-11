package it.unibo.disi.spaf.storm.connectors.rabbitmq;

import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;

import com.rabbitmq.client.ConnectionFactory;

import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSink;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSource;
import it.unibo.disi.spaf.storm.connectors.StormConnectorsFactory;
import it.unibo.disi.spaf.storm.connectors.rabbitmq.bolt.ElementTupleToRabbitMQMessageConverter;
import it.unibo.disi.spaf.storm.connectors.rabbitmq.spout.ElementMessageScheme;
import ru.burov4j.storm.rabbitmq.RabbitMqBolt;
import ru.burov4j.storm.rabbitmq.RabbitMqConfig;
import ru.burov4j.storm.rabbitmq.RabbitMqConfigBuilder;
import ru.burov4j.storm.rabbitmq.RabbitMqMessageScheme;
import ru.burov4j.storm.rabbitmq.RabbitMqSpout;
import ru.burov4j.storm.rabbitmq.TupleToRabbitMqMessageConverter;

public class RabbitMQStormConnectorsFactory extends StormConnectorsFactory {

	private static RabbitMQStormConnectorsFactory instance;

	private RabbitMQStormConnectorsFactory() {}

	public static final RabbitMQStormConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new RabbitMQStormConnectorsFactory();
		}

		return instance;
	}

	@Override
	public void setupSourceSpout(TopologyBuilder topologyBuilder, Source<?, ?> source, String sourceName) {
		RabbitMQSource<?, ?> rabbitMQSourceDescriptor = (RabbitMQSource<?, ?>) source;

		String host = rabbitMQSourceDescriptor.getHost();
		int port = rabbitMQSourceDescriptor.getPort();
		String username = rabbitMQSourceDescriptor.getUsername();
		String password = rabbitMQSourceDescriptor.getPassword();
		String queue = rabbitMQSourceDescriptor.getQueue();
		String idDeserializer = rabbitMQSourceDescriptor.getIdDeserializer();
		String bodyDeserializer = rabbitMQSourceDescriptor.getBodyDeserializer();
		
		RabbitMqConfig rabbitMqConfig = buildRabbitMqConfig(host, port, username, password);

		RabbitMqMessageScheme elementScheme = new ElementMessageScheme(idDeserializer, bodyDeserializer);
		IRichSpout spout = new RabbitMqSpout(rabbitMqConfig, elementScheme);

		topologyBuilder
			.setSpout(sourceName, spout)
			.addConfiguration(RabbitMqSpout.KEY_QUEUE_NAME, queue)
			.addConfiguration(RabbitMqSpout.KEY_AUTO_ACK, true) // TODO: from config?
			.addConfiguration(RabbitMqSpout.KEY_PREFETCH_COUNT, 64) // TODO: from config?
		    .addConfiguration(RabbitMqSpout.KEY_REQUEUE_ON_FAIL, true); // TODO: from config?
	}

	@Override
	public void setupSinkBolt(TopologyBuilder topologyBuilder, Sink<?, ?> sink, String sinkName, String previousComponentId) {
		RabbitMQSink<?, ?> rabbitMQSinkDescriptor = (RabbitMQSink<?, ?>) sink;

		String host = rabbitMQSinkDescriptor.getHost();
		int port = rabbitMQSinkDescriptor.getPort();
		String username = rabbitMQSinkDescriptor.getUsername();
		String password = rabbitMQSinkDescriptor.getPassword();
		String queue = rabbitMQSinkDescriptor.getQueue();
		String idSerializerClassName = rabbitMQSinkDescriptor.getIdSerializer();
		String bodySerializerClassName = rabbitMQSinkDescriptor.getBodySerializer();

		RabbitMqConfig rabbitMqConfig = buildRabbitMqConfig(host, port, username, password);

		TupleToRabbitMqMessageConverter messageConverter = new ElementTupleToRabbitMQMessageConverter(queue, idSerializerClassName, bodySerializerClassName);
		IRichBolt sinkBolt = new RabbitMqBolt(rabbitMqConfig, messageConverter);

		topologyBuilder
			.setBolt(sinkName, sinkBolt)
			.shuffleGrouping(previousComponentId);
	}

	private RabbitMqConfig buildRabbitMqConfig(String host, int port, String username, String password) {
		return new RabbitMqConfigBuilder()
				.setHost(host)
				.setPort(port)
				.setUsername(username)
				.setPassword(password)
				.setVirtualHost(ConnectionFactory.DEFAULT_VHOST) // TODO: from config?
				.setRequestedHeartbeat(10) // TODO: make it configurable?
				.build();
	}

}
