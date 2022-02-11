package it.unibo.disi.spaf.flink.streaming.helper;

import org.apache.flink.streaming.api.functions.ProcessFunction;

import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.internals.Element;

public class ProcessorProcessFunctionFactory {

	private static ProcessorProcessFunctionFactory instance;

	private ProcessorProcessFunctionFactory() {
		super();
	}

	public static final ProcessorProcessFunctionFactory getInstance() {
		if (instance == null) {
			instance = new ProcessorProcessFunctionFactory();
		}

		return instance;
	}
	
	public ProcessFunction<Element<Object, Object>, Element<Object, Object>> build(Processor<Object, Object, Object, Object> processor) {
		
		ProcessFunction<Element<Object, Object>, Element<Object, Object>> processFunction = new ProcessorProcessFunction(processor);
		
		return processFunction;
	}

}
