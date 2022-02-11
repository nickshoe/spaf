package it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer;

import java.io.Serializable;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class PooledSparkKafkaProducerFactory<K, V> extends BasePooledObjectFactory<SparkKafkaProducer<K, V>> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final SparkKafkaProducerFactory factory;

	public PooledSparkKafkaProducerFactory(SparkKafkaProducerFactory factory) {
		super();
		this.factory = factory;
	}

	@Override
	public SparkKafkaProducer<K, V> create() {
		return this.factory.newInstance();
	}

	@Override
	public PooledObject<SparkKafkaProducer<K, V>> wrap(SparkKafkaProducer<K, V> obj) {
		return new DefaultPooledObject<SparkKafkaProducer<K, V>>(obj);
	}

	@Override
	public void destroyObject(PooledObject<SparkKafkaProducer<K, V>> p) throws Exception {
		p.getObject().close();

		super.destroyObject(p);
	}

}
