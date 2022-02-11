package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

import java.nio.charset.StandardCharsets;

public class StringBodyDeserializer implements BodyDeserializer<String> {

	@Override
	public String deserialize(byte[] data) {
		if (data == null) {
            return null;
		} else {
			// TODO: let the user specify a different charset? (like Kafka does..)
			return new String(data, StandardCharsets.UTF_8);
		}
	}

}
