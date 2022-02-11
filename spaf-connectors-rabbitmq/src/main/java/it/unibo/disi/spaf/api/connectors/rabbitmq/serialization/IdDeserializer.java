package it.unibo.disi.spaf.api.connectors.rabbitmq.serialization;

public interface IdDeserializer<T> {

	T deserialize(String data);
	
}
