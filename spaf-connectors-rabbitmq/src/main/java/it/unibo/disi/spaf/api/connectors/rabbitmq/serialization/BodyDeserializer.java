package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

public interface BodyDeserializer<T> {

	T deserialize(byte[] data);
	
}
