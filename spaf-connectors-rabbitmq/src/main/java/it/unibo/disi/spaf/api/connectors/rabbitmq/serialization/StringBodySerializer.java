package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

import java.nio.charset.StandardCharsets;

public class StringBodySerializer implements BodySerializer<String> {

	@Override
	public byte[] serialize(String data) {
		if (data == null) {
        	return null;	
        } else {
        	// TODO: let the user specify a different charset? (like Kafka does..)
        	return data.getBytes(StandardCharsets.UTF_8);
        }
	}

}
