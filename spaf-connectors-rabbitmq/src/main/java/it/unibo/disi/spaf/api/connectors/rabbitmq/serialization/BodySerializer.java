package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

public interface BodySerializer<T> {

	byte[] serialize(T data);
	
}
