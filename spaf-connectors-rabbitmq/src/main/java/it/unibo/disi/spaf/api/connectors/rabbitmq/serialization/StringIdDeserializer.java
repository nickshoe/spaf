package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

public class StringIdDeserializer implements IdDeserializer<String> {

	@Override
	public String deserialize(String data) {
		return data;
	}

}
