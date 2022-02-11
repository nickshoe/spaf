package it.unibo.disi.spaf.storm.connectors.rabbitmq.spout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Fields;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.BodyDeserializer;
import it.unibo.disi.spaf.api.connectors.rabbitmq.serialization.IdDeserializer;
import it.unibo.disi.spaf.common.utils.ReflectionUtils;
import ru.burov4j.storm.rabbitmq.ConvertionException;
import ru.burov4j.storm.rabbitmq.SingleStreamRabbitMqMessageScheme;

public class ElementMessageScheme extends SingleStreamRabbitMqMessageScheme {

	private static final long serialVersionUID = 1L;

	private final String idDeserializerClassName;
	private final String bodyDeserializerClassName;

	private IdDeserializer<?> idDeserializer;
	private BodyDeserializer<?> bodyDeserializer;

	public ElementMessageScheme(String idDeserializerClassName, String bodyDeserializerClassName) {
		super();
		this.idDeserializerClassName = idDeserializerClassName;
		this.bodyDeserializerClassName = bodyDeserializerClassName;
	}

	@Override
	public void prepare(Map config, TopologyContext context) {
		this.idDeserializer =  ReflectionUtils.getInstance(this.idDeserializerClassName, IdDeserializer.class);
		this.bodyDeserializer = ReflectionUtils.getInstance(this.bodyDeserializerClassName, BodyDeserializer.class);
	}

	@Override
	public void cleanup() {
		this.idDeserializer = null;
		this.bodyDeserializer = null;
	}

	@Override
	public List<Object> convertToTuple(Envelope envelope, BasicProperties properties, byte[] body) throws ConvertionException {
		String messageId = properties.getMessageId();
		
		Object key = idDeserializer.deserialize(messageId);
		Object value = bodyDeserializer.deserialize(body);
		
		List<Object> tuple = new ArrayList<>();
		
		tuple.add(key);
		tuple.add(value);
		
		return tuple;
	}

	@Override
	public Fields getOutputFields() {
		return new Fields("key", "value");
	}

}
