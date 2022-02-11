package it.unibo.disi.spaf.flink.streaming.connectors.kafka.serialization;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.kafka.common.serialization.Serializer;

import it.unibo.disi.spaf.common.utils.ReflectionUtils;
import it.unibo.disi.spaf.internals.Element;

// TODO: use type param
public class ElementValueSerializationSchema implements SerializationSchema<Element<Object, Object>> {

	private static final long serialVersionUID = 1L;

	private final String topic;
	private final String serializerClassNames;

	private Serializer<Object> serializer; // TODO: use type param from class

	public ElementValueSerializationSchema(String topic, String serializerClassNames) {
		super();
		this.topic = topic;
		this.serializerClassNames = serializerClassNames;
	}

	@Override
	public byte[] serialize(Element<Object, Object> element) {
		if (this.serializer == null) {
			// TODO: this warning will go away as soon we'll support type param
			this.serializer = ReflectionUtils.getInstance(serializerClassNames, Serializer.class);
		}
		
		return this.serializer.serialize(this.topic, element.getValue());
	}

}
