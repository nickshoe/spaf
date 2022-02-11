package it.unibo.disi.spaf.storm.bolts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.internals.CollectorImpl;
import it.unibo.disi.spaf.internals.Element;

public class ProcessorBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private final Processor<Object, Object, Object, Object> processor;
	
	private OutputCollector outputCollector;
	
	public ProcessorBolt(Processor<Object, Object, Object, Object> processor) {
		super();
		this.processor = processor;
	}

	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		this.outputCollector = collector;
		
		this.processor.init();
	}

	@Override
	public void execute(Tuple input) {
		Object key = input.getValueByField("key");
		Object value = input.getValueByField("value");
		
		Element<Object, Object> element = new Element<>(key, value);
		
		Collector<Object, Object> collector = new CollectorImpl<>();
		processor.process(element.getKey(), element.getValue(), collector);
		
		for (Element<Object, Object> outputElement : collector) {
			List<Object> outputTupleValues = new ArrayList<>();
		
			outputTupleValues.add(outputElement.getKey());
			outputTupleValues.add(outputElement.getValue());
		
			outputCollector.emit(input, outputTupleValues);
		}
		
		outputCollector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("key", "value"));
	}

}
