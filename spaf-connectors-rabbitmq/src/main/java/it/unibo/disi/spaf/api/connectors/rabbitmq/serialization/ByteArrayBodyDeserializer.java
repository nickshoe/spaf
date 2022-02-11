package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

public class ByteArrayBodyDeserializer implements BodyDeserializer<byte[]> {

	@Override
	public byte[] deserialize(byte[] data) {
		return data;
	}

}
