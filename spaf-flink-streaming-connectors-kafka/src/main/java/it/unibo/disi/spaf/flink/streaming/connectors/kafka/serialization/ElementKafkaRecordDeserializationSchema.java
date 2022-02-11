package it.unibo.disi.spaf.flink.streaming.connectors.kafka.serialization;

import java.io.IOException;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Deserializer;

import it.unibo.disi.spaf.common.utils.ReflectionUtils;
import it.unibo.disi.spaf.internals.Element;

public class ElementKafkaRecordDeserializationSchema implements KafkaRecordDeserializationSchema<Element<Object, Object>> {

	private static final long serialVersionUID = 1L;

	private final String topic;
	private final String keyDeserializerClassNames;
	private final String valueDeserializerClassName;
	
	private Deserializer<?> keyDeserializer;
	private Deserializer<?> valueDeserializer;
	
	public ElementKafkaRecordDeserializationSchema(
		String topic, 
		String keyDeserializerClassNames,
		String valueDeserializerClassName
	) {
		super();
		this.topic = topic;
		this.keyDeserializerClassNames = keyDeserializerClassNames;
		this.valueDeserializerClassName = valueDeserializerClassName;
	}

	@Override
	public TypeInformation<Element<Object, Object>> getProducedType() {
		return TypeInformation.of(new TypeHint<Element<Object,Object>>(){});
	}

	@Override
	public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<Element<Object, Object>> out) throws IOException {
		if (this.keyDeserializer == null) {
			this.keyDeserializer = ReflectionUtils.getInstance(this.keyDeserializerClassNames, Deserializer.class);
		}
		if (this.valueDeserializer == null) {
			this.valueDeserializer = ReflectionUtils.getInstance(this.valueDeserializerClassName, Deserializer.class);
		}
		
		Object key = keyDeserializer.deserialize(this.topic, record.key());
		Object value = valueDeserializer.deserialize(this.topic, record.value());
		
		Element<Object, Object> element = new Element<Object, Object>(key, value);
		
		out.collect(element);
	}

}
