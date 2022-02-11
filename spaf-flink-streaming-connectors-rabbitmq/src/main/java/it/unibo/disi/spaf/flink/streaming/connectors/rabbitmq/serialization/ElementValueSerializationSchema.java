package it.unibo.disi.spaf.flink.streaming.connectors.rabbitmq.serialization;

import org.apache.flink.api.common.serialization.SerializationSchema;

import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.BodySerializer;
import it.unibo.disi.spaf.common.utils.ReflectionUtils;
import it.unibo.disi.spaf.internals.Element;

public class ElementValueSerializationSchema implements SerializationSchema<Element<Object, Object>> {

	private BodySerializer<Object> bodySerializer;

	private static final long serialVersionUID = 1L;

	private final String bodySerializerClassName;

	public ElementValueSerializationSchema(String bodySerializerClassName) {
		super();
		this.bodySerializerClassName = bodySerializerClassName;
	}

	@Override
	public byte[] serialize(Element<Object, Object> element) {
		if (this.bodySerializer == null) {
			// TODO: need type param
			this.bodySerializer = ReflectionUtils.getInstance(bodySerializerClassName, BodySerializer.class);
		}

		return this.bodySerializer.serialize(element.getValue());
	}

}
