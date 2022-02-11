package it.unibo.disi.spaf.api;

import java.io.Serializable;
import java.util.List;

// TODO: wrap typesafe exceptions
public class Config implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final com.typesafe.config.Config config;

	Config(com.typesafe.config.Config config) {
		super();
		this.config = config;
	}
	
	public boolean hasPath(String path) {
		return this.config.hasPath(path);
	}
	
	public String getString(String path) {
		return this.config.getString(path);
	}
	
	public int getInt(String path) {
		return this.config.getInt(path);
	}
	
	public long getLong(String path) {
		return this.config.getLong(path);
	}
	
	public boolean getBoolean(String path) {
		return this.config.getBoolean(path);
	}
	
	public List<String> getStringList(String path) {
		return this.config.getStringList(path);
	}
	
	public Config getSubConfig(String path) {
		return new Config(this.config.getObject(path).toConfig());
	}
	
}
