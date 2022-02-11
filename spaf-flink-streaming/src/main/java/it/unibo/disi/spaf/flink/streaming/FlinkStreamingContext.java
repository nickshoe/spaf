package it.unibo.disi.spaf.flink.streaming;

import org.apache.flink.runtime.client.JobCancellationException;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.flink.streaming.connectors.FlinkStreamingConnectors;
import it.unibo.disi.spaf.flink.streaming.exceptions.FlinkStreamingStreamProcessingException;
import it.unibo.disi.spaf.flink.streaming.helper.FlinkExecutionEnvironmentFactory;
import it.unibo.disi.spaf.flink.streaming.helper.ProcessorProcessFunctionFactory;
import it.unibo.disi.spaf.internals.Element;
import it.unibo.disi.spaf.internals.OneTimeInitProcessor;

public class FlinkStreamingContext implements Context {

	private final static Logger logger = LoggerFactory.getLogger(FlinkStreamingContext.class);

	private final Config config;

	protected FlinkStreamingContext(Config config) {
		super();
		this.config = config;
	}

	@Override
	public void run(Application application) {
		logger.info("Running application {}", application.getName());
		
		// 1. Configuring and obtaining the execution context
		StreamExecutionEnvironment env = FlinkExecutionEnvironmentFactory.getInstance().build(config);

		// 2. Mapping SPAF Source to Flink Source
		Source<?, ?> source = application.getTopology().getSource();
		DataStream<Element<Object, Object>> stream = FlinkStreamingConnectors.createDataStream(env, application, source);
		
		// 3. Mapping SPAF Topology to Flink Streaming topology
		// TODO: abstract the way the SPAF topology gets "visited", so it will be easier to evolve to DAG topologies in the future (see Visitor pattern)
		for (Processor<?, ?, ?, ?> processor : application.getTopology().getProcessors()) {
			// TODO: is there any way to avoid this unchecked cast?
			Processor<Object, Object, Object, Object> p = (Processor<Object, Object, Object, Object>) processor;
			Processor<Object, Object, Object, Object> processorWrapper = new OneTimeInitProcessor<>(p);
			
			ProcessFunction<Element<Object, Object>, Element<Object, Object>> processorProcessFunction = ProcessorProcessFunctionFactory.getInstance().build(processorWrapper);
			
			stream = stream.process(processorProcessFunction);
		}
		
		// 4. Mapping SPAF Sink to Spark Streaming output 
		Sink<?, ?> sink = application.getTopology().getSink();
		FlinkStreamingConnectors.setupStreamOutput(stream, sink);
		
		// 5. Application launch
		logger.info("Submitting application {} to Flink's streaming execution environment...", application.getName());
		try {
			env.execute(application.getName());
		} catch (JobCancellationException e) {
			logger.info("The application has been stopped because the corresponding Flink jobs have been deleted");
		} catch (Exception e) {
			throw new FlinkStreamingStreamProcessingException(e);
		}
	}
	
}
