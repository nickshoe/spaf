package it.unibo.disi.spaf.flink.streaming.helper;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;

import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.internals.CollectorImpl;
import it.unibo.disi.spaf.internals.Element;

public class ProcessorProcessFunction extends ProcessFunction<Element<Object, Object>, Element<Object, Object>> {

	private static final long serialVersionUID = 1L;

	private final Processor<Object, Object, Object, Object> processor;

	protected ProcessorProcessFunction(Processor<Object, Object, Object, Object> processor) {
		super();
		this.processor = processor;
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		super.open(parameters);

		processor.init();
	}

	@Override
	public void processElement(
		Element<Object, Object> element,
		ProcessFunction<Element<Object, Object>, Element<Object, Object>>.Context ctx,
		org.apache.flink.util.Collector<Element<Object, Object>> out
	) throws Exception {
		Collector<Object, Object> collector = new CollectorImpl<>();
		processor.process(element.getKey(), element.getValue(), collector);

		for (Element<Object, Object> outputElement : collector) {
			out.collect(outputElement);
		}
	}

}
