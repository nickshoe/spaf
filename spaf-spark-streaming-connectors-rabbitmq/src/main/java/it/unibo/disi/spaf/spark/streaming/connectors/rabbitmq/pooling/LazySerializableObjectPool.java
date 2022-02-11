package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.pooling;

import org.apache.commons.pool2.ObjectPool;

import java.io.Serializable;

public abstract class LazySerializableObjectPool<T> implements ObjectPool<T>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient ObjectPool<T> delegate;

	protected abstract ObjectPool<T> createDelegate();

	private ObjectPool<T> lazyDalagate() {
		if (this.delegate == null) {
			this.delegate = createDelegate();
		}
		return this.delegate;
	}

	@Override
	public T borrowObject() throws Exception {
		return lazyDalagate().borrowObject();
	}

	@Override
	public void returnObject(T obj) throws Exception {
		lazyDalagate().returnObject(obj);
	}

	@Override
	public void invalidateObject(T obj) throws Exception {
		lazyDalagate().invalidateObject(obj);
	}

	@Override
	public void addObject() throws Exception {
		lazyDalagate().addObject();
	}

	@Override
	public int getNumIdle() {
		return lazyDalagate().getNumIdle();
	}

	@Override
	public int getNumActive() {
		return lazyDalagate().getNumActive();
	}
	
	@Override
	public void clear() throws Exception {
		lazyDalagate().clear();		
	}
	
	@Override
	public void close() {
		lazyDalagate().close();
	}
	
}
