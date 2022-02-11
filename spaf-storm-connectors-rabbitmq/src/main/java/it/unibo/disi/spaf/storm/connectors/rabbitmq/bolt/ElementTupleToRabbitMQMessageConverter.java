package it.unibo.disi.spaf.storm.connectors.rabbitmq.bolt;

import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;

import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.BodySerializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.IdSerializer;
import it.unibo.disi.spaf.common.utils.ReflectionUtils;
import ru.burov4j.storm.rabbitmq.ConvertionException;
import ru.burov4j.storm.rabbitmq.TupleToRabbitMqMessageConverter;

public class ElementTupleToRabbitMQMessageConverter implements TupleToRabbitMqMessageConverter {

	private static final long serialVersionUID = 1L;

	private final String queueName;
	private final String idSerializerClassName;
	private final String bodySerializerClassName;

	private IdSerializer<Object> idSerializer;
	private BodySerializer<Object> bodySerializer;

	public ElementTupleToRabbitMQMessageConverter(String queueName, String idSerializerClassName, String bodySerializerClassName) {
		super();
		this.queueName = queueName;
		this.idSerializerClassName = idSerializerClassName;
		this.bodySerializerClassName = bodySerializerClassName;
	}

	@Override
	public void prepare(Map config, TopologyContext context) {
		this.idSerializer = ReflectionUtils.getInstance(idSerializerClassName, IdSerializer.class);
		this.bodySerializer = ReflectionUtils.getInstance(bodySerializerClassName, BodySerializer.class);
	}

	@Override
	public String getExchange(Tuple tuple) throws ConvertionException {
		return ""; // TODO: make it configurable?
	}

	@Override
	public String getRoutingKey(Tuple tuple) throws ConvertionException {
		return this.queueName;
	}

	@Override
	public BasicProperties getProperties(Tuple tuple) throws ConvertionException {
		Object key = tuple.getValueByField("key");

		String messageId = this.idSerializer.serialize(key);
		
		AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().messageId(messageId).build();
		
		return properties;
	}

	@Override
	public byte[] getMessageBody(Tuple tuple) throws ConvertionException {
		Object value = tuple.getValueByField("value");

		byte[] messageBody = this.bodySerializer.serialize(value);
		
		return messageBody;
	}

	@Override
	public void cleanup() {
		this.idSerializer = null;
		this.bodySerializer = null;
	}

}
