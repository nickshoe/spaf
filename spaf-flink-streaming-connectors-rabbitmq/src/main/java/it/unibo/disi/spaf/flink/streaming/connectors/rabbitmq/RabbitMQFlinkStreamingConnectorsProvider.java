package it.unibo.disi.spaf.flink.streaming.connectors.rabbitmq;
import it.unibo.disi.spaf.flink.streaming.connectors.FlinkStreamingConnectorsFactory;
import it.unibo.disi.spaf.flink.streaming.connectors.spi.FlinkStreamingConnectorsProvider;

public class RabbitMQFlinkStreamingConnectorsProvider implements FlinkStreamingConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "rabbitmq";
	}

	@Override
	public FlinkStreamingConnectorsFactory createConnectorsFactory() {
		return RabbitMQFlinkStreamingConnectorsFactory.getInstance();
	}

}
