package it.unibo.disi.spaf.flink.streaming.connectors.rabbitmq.serialization;

import java.io.IOException;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.rabbitmq.RMQDeserializationSchema;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.BodyDeserializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.IdDeserializer;
import it.unibo.disi.spaf.common.utils.ReflectionUtils;
import it.unibo.disi.spaf.internals.Element;

public class ElementRabbitMQDeserializationSchema implements RMQDeserializationSchema<Element<Object, Object>> {

	private static final long serialVersionUID = 1L;

	private final String idDeserializerClassName;
	private final String bodyDeserializerClassName;
	
	private IdDeserializer<?> idDeserializer;
	private BodyDeserializer<?> bodyDeserializer;
	
	public ElementRabbitMQDeserializationSchema(String idDeserializerClassName, String bodyDeserializerClassName) {
		super();
		this.idDeserializerClassName = idDeserializerClassName;
		this.bodyDeserializerClassName = bodyDeserializerClassName;
	}

	@Override
	public TypeInformation<Element<Object, Object>> getProducedType() {
		return TypeInformation.of(new TypeHint<Element<Object, Object>>() {});
	}

	@Override
	public void deserialize(
		Envelope envelope, 
		BasicProperties properties, 
		byte[] body,
		RMQCollector<Element<Object, Object>> collector
	) throws IOException {		
		if (this.idDeserializer == null) {
			this.idDeserializer =  ReflectionUtils.getInstance(this.idDeserializerClassName, IdDeserializer.class);
		}
		if (this.bodyDeserializer == null) {
			this.bodyDeserializer = ReflectionUtils.getInstance(this.bodyDeserializerClassName, BodyDeserializer.class);
		}
		
		String messageId = properties.getMessageId();
		
		// Transform RabbitMQ message into a SPAF element
		Object key = idDeserializer.deserialize(messageId);
		Object value = bodyDeserializer.deserialize(body);
		Element<Object, Object> element = new Element<Object, Object>(key, value);
		
		collector.collect(element);
	}

	@Override
	public boolean isEndOfStream(Element<Object, Object> nextElement) {
		return false;
	}

}
