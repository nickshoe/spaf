package it.unibo.disi.spaf.flink.streaming.connectors.rabbitmq.serialization;

import org.apache.flink.streaming.connectors.rabbitmq.RMQSinkPublishOptions;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;

import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.IdSerializer;
import it.unibo.disi.spaf.common.utils.ReflectionUtils;
import it.unibo.disi.spaf.internals.Element;

public class ElementRMQSinkPublishOptions implements RMQSinkPublishOptions<Element<Object, Object>> {

	private static final long serialVersionUID = 1L;

	private IdSerializer<Object> idSerializer;

	private final String queueName;
	private final String idSerializerClassName;

	public ElementRMQSinkPublishOptions(String queueName, String idSerializerClassName) {
		super();
		this.queueName = queueName;
		this.idSerializerClassName = idSerializerClassName;
	}

	@Override
	public String computeRoutingKey(Element<Object, Object> a) {
		return this.queueName;
	}

	@Override
	public BasicProperties computeProperties(Element<Object, Object> element) {
		if (this.idSerializer == null) {
			// TODO: need type param
			this.idSerializer = ReflectionUtils.getInstance(idSerializerClassName, IdSerializer.class);
		}

		String messageId = this.idSerializer.serialize(element.getKey());

		AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().messageId(messageId).build();

		return properties;
	}

	@Override
	public String computeExchange(Element<Object, Object> a) {
		return ""; // TODO: make it configurable?
	}

}
