package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

public class StringIdSerializer implements IdSerializer<String> {

	@Override
	public String serialize(String data) {
		return data;
	}

}
