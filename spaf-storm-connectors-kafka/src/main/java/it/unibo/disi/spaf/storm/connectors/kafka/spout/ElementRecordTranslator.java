package it.unibo.disi.spaf.storm.connectors.kafka.spout;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.kafka.spout.KafkaTuple;
import org.apache.storm.kafka.spout.RecordTranslator;
import org.apache.storm.tuple.Fields;

public class ElementRecordTranslator implements RecordTranslator<Object, Object> {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Fields getFieldsFor(String stream) {
		return new Fields("key", "value");
	}
	
	@Override
	public List<Object> apply(ConsumerRecord<Object, Object> record) {
		Object key = record.key();
		Object value = record.value();
		
		return new KafkaTuple(key, value).routedTo("default");
	}
	
}
