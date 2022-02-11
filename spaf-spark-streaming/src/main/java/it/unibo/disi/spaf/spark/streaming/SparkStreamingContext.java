package it.unibo.disi.spaf.spark.streaming;

import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.internals.CollectorImpl;
import it.unibo.disi.spaf.internals.Element;
import it.unibo.disi.spaf.internals.OneTimeInitProcessor;
import it.unibo.disi.spaf.spark.streaming.connectors.SparkStreamingConnectors;
import it.unibo.disi.spaf.spark.streaming.helper.SparkJavaStreamingContextFactory;

public class SparkStreamingContext implements Context {

	private final static Logger logger = LoggerFactory.getLogger(SparkStreamingContext.class);

	private final Config config;

	public SparkStreamingContext(Config config) {
		super();
		this.config = config;
	}

	@Override
	public void run(Application application) {
		logger.info("Running application {}", application.getName());

		// 1. Configuring and obtaining the execution context
		JavaStreamingContext javaStreamingContext = SparkJavaStreamingContextFactory.getInstance().build(this.config, application);

		// 2. Mapping SPAF Source to SparkStreaming InputStream
		Source<?, ?> source = application.getTopology().getSource();
		JavaDStream<Element<Object, Object>> stream = SparkStreamingConnectors.createJavaDStream(javaStreamingContext, application, source);
		
		// 3. Mapping SPAF Topology to Spark Streaming topology
		// TODO: abstract the way the SPAF topology gets "visited", so it will be easier to evolve to DAG topologies in the future (see Visitor pattern)
		for (Processor<?, ?, ?, ?> processor : application.getTopology().getProcessors()) {
			// TODO: is there any way to avoid this unchecked cast?
			Processor<Object, Object, Object, Object> p = (Processor<Object, Object, Object, Object>) processor;
			Processor<Object, Object, Object, Object> processorWrapper = new OneTimeInitProcessor<>(p);

			// Each processor is broadcasted, so it can be initialized only once (lazily, to avoid serialization problems) by each executor
			Broadcast<Processor<Object, Object, Object, Object>> processorWrapperVar = javaStreamingContext.sparkContext().broadcast(processorWrapper);
			
			stream = stream.transform(rdd -> { // This code will be executed in the driver				
				return rdd.flatMap(inputElement -> { // This code will be executed in the workers
					Processor<Object, Object, Object, Object> localProcessorWrapper = processorWrapperVar.value();
					
					localProcessorWrapper.init();
					
					Collector<Object, Object> collector = new CollectorImpl<>();
					
					localProcessorWrapper.process(inputElement.getKey(), inputElement.getValue(), collector);

					return collector.iterator();
				});
			});
		}

		// 4. Mapping SPAF Sink to Spark Streaming output 
		Sink<?, ?> sink = application.getTopology().getSink();
		SparkStreamingConnectors.setupStreamOutput(javaStreamingContext, stream, sink);

		// 5. Application launch
		logger.info("Starting Spark Streaming context...");
		javaStreamingContext.start();
		
		try {
			javaStreamingContext.awaitTermination();
		} catch (InterruptedException e) {
			logger.error("Spark Streaming Context was abruptly interrupted by an error: {}", e.getMessage());
		}
		logger.info("Spark Streaming context terminated.");
	}

}
