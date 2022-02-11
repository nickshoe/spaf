package it.unibo.disi.spaf.samza.connectors.rabbitmq.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.samza.serializers.ByteSerde;
import org.apache.samza.serializers.KVSerde;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.StringSerde;

import io.github.nickshoe.samza.system.rabbitmq.descriptors.RabbitMQSystemDescriptor;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.ByteArrayBodyDeserializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.ByteArrayBodySerializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.StringBodyDeserializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.StringBodySerializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.StringIdDeserializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.StringIdSerializer;
import it.unibo.disi.spaf.samza.exceptions.SamzaStreamProcessingException;

public class RabbitMQUtils {
	
	private static final Map<String, Serde<?>> AVAILABLE_ID_SERDES = new HashMap<>();
	
	private static final Map<String, Serde<?>> AVAILABLE_BODY_SERDES = new HashMap<>();

	static {
		AVAILABLE_ID_SERDES.put(StringIdSerializer.class.getName(), new StringSerde());
		AVAILABLE_ID_SERDES.put(StringIdDeserializer.class.getName(), new StringSerde());
		
		AVAILABLE_BODY_SERDES.put(StringBodySerializer.class.getName(), new StringSerde());
		AVAILABLE_BODY_SERDES.put(StringBodyDeserializer.class.getName(), new StringSerde());
		AVAILABLE_BODY_SERDES.put(ByteArrayBodySerializer.class.getName(), new ByteSerde());
		AVAILABLE_BODY_SERDES.put(ByteArrayBodyDeserializer.class.getName(), new ByteSerde());
	}
	
	public static final String RABBITMQ_SYSTEM_NAME = "rabbitmq";
	
	public static RabbitMQSystemDescriptor buildRabbitMQSystemDescriptor(String host, int port, String username, String password) {
		RabbitMQSystemDescriptor rabbitMQSystemDescriptor = new RabbitMQSystemDescriptor(RABBITMQ_SYSTEM_NAME)
			.withHost(host)
			.withPort(port)
			.withUsername(username)
			.withPassword(password);
		
		return rabbitMQSystemDescriptor;
	}
	
	public static final <K, V> KVSerde<K, V> buildKVSerde(
		String idSerdeClassName,
		String bodySerdeClassName
	) {
		if (!AVAILABLE_ID_SERDES.containsKey(idSerdeClassName)) {
			throw new SamzaStreamProcessingException("No available serde for id serializer/deserializer class " + idSerdeClassName);
		}
		if (!AVAILABLE_BODY_SERDES.containsKey(bodySerdeClassName)) {
			throw new SamzaStreamProcessingException("No available serde for body serializer/deserializer class " + bodySerdeClassName);
		}

		Serde<?> keySerde = AVAILABLE_ID_SERDES.get(idSerdeClassName);
		Serde<?> valueSerde = AVAILABLE_BODY_SERDES.get(bodySerdeClassName);

		KVSerde<K, V> kvSerde = (KVSerde<K, V>) KVSerde.of(keySerde, valueSerde);

		return kvSerde;
	}
		
}
