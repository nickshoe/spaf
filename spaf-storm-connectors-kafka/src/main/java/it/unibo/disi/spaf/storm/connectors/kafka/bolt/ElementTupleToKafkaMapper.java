package it.unibo.disi.spaf.storm.connectors.kafka.bolt;

import org.apache.storm.kafka.bolt.mapper.TupleToKafkaMapper;
import org.apache.storm.tuple.Tuple;

public class ElementTupleToKafkaMapper implements TupleToKafkaMapper<Object, Object> {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object getKeyFromTuple(Tuple tuple) {
		Object key = tuple.getValueByField("key");
		
		return key;
	}

	@Override
	public Object getMessageFromTuple(Tuple tuple) {
		Object value = tuple.getValueByField("value");
		
		return value;
	}

}
