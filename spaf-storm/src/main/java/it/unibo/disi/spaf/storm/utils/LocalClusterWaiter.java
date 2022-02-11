package it.unibo.disi.spaf.storm.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * class copied from org.apache.spark.streaming.ContextWaiter
 */
public class LocalClusterWaiter {

	private Lock lock = new ReentrantLock();
	private Condition lockCondition = lock.newCondition();
	
	// Guarded by "lock"
	private Throwable error = null;
	
	// Guarded by "lock"
	private boolean stopped = false;
	
	public void notifyError(Throwable error) {
		lock.lock();
		try {
			this.error = error;
			this.lockCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public void notifyStop() {
		lock.lock();
		try {
			this.stopped = true;
			this.lockCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public boolean waitForStopOrError() throws Throwable {
		return this.waitForStopOrError(-1);
	}
	
	/**
	 * Return `true` if it's stopped; or throw the reported error if `notifyError`
	 * has been called; or `false` if the waiting time detectably elapsed before
	 * return from the method.
	 */
	public boolean waitForStopOrError(long timeout) throws Throwable {
		lock.lock();
		
		try {
			if (timeout < 0) {
				while (!stopped && error == null) {
					lockCondition.await();
				}
			} else {
				long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
				while (!stopped && error == null && nanos > 0) {
					nanos = lockCondition.awaitNanos(nanos);
				}
			}
			// If already had error, then throw it
			if (error != null) {
				throw error;
			}
			// already stopped or timeout
			return stopped;
		} finally {
			lock.unlock();
		}
	}
}
