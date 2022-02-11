package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

public class ByteArrayBodySerializer implements BodySerializer<byte[]> {

	@Override
	public byte[] serialize(byte[] data) {
		return data;
	}

}
