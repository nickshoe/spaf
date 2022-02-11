package it.unibo.disi.spaf.api;

public class Application {
	private String name;
	private Topology topology;

	public Application() {
		super();
	}

	public Application withName(String name) {
		this.name = name;

		return this;
	}

	public Application withTopology(Topology topology) {
		this.topology = topology;

		return this;
	}

	public String getName() {
		return this.name;
	}

	public Topology getTopology() {
		return topology;
	}

}
