package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

public interface IdSerializer<T> {

	String serialize(T data);
	
}
