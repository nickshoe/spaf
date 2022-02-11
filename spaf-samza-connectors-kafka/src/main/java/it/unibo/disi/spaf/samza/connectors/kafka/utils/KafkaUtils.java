package it.unibo.disi.spaf.samza.connectors.kafka.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.samza.serializers.ByteSerde;
import org.apache.samza.serializers.KVSerde;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.StringSerde;
import org.apache.samza.system.kafka.descriptors.KafkaSystemDescriptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import it.unibo.disi.spaf.samza.exceptions.SamzaStreamProcessingException;

public class KafkaUtils {

	private static final Map<String, Serde<?>> AVAILABLE_SERDES = new HashMap<>();

	static {
		AVAILABLE_SERDES.put(StringSerializer.class.getName(), new StringSerde());
		AVAILABLE_SERDES.put(StringDeserializer.class.getName(), new StringSerde());
		AVAILABLE_SERDES.put(ByteArraySerializer.class.getName(), new ByteSerde());
		AVAILABLE_SERDES.put(ByteArrayDeserializer.class.getName(), new ByteSerde());
	}
	
	public static final String KAFKA_SYSTEM_NAME = "kafka";

	public static final KafkaSystemDescriptor buildKafkaSystemDescriptor(
		Optional<String> zookeeperConnect,
		String bootstrapServers
	) {
		final List<String> KAFKA_PRODUCER_BOOTSTRAP_SERVERS = ImmutableList.of(bootstrapServers);
		// TODO: introduce config?
		final Map<String, String> KAFKA_DEFAULT_STREAM_CONFIGS = ImmutableMap.of("replication.factor", "1");

		KafkaSystemDescriptor kafkaSystemDescriptor = new KafkaSystemDescriptor(KAFKA_SYSTEM_NAME)
				.withProducerBootstrapServers(KAFKA_PRODUCER_BOOTSTRAP_SERVERS)
				.withDefaultStreamConfigs(KAFKA_DEFAULT_STREAM_CONFIGS);

		if (zookeeperConnect.isPresent()) {
			final List<String> KAFKA_CONSUMER_ZK_CONNECT = ImmutableList.of(zookeeperConnect.get());

			kafkaSystemDescriptor.withConsumerZkConnect(KAFKA_CONSUMER_ZK_CONNECT);
		}

		return kafkaSystemDescriptor;
	}

	public static final <K, V> KVSerde<K, V> buildKVSerde(
		String keySerdeClassName,
		String valueSerdeClassName
	) {
		if (!AVAILABLE_SERDES.containsKey(keySerdeClassName)) {
			throw new SamzaStreamProcessingException("No available serde for key serializer/deserializer class " + keySerdeClassName);
		}
		if (!AVAILABLE_SERDES.containsKey(valueSerdeClassName)) {
			throw new SamzaStreamProcessingException("No available serde for value serializer/deserializer class " + valueSerdeClassName);
		}

		Serde<?> keySerde = AVAILABLE_SERDES.get(keySerdeClassName);
		Serde<?> valueSerde = AVAILABLE_SERDES.get(valueSerdeClassName);

		KVSerde<K, V> kvSerde = (KVSerde<K, V>) KVSerde.of(keySerde, valueSerde);

		return kvSerde;
	}

}
