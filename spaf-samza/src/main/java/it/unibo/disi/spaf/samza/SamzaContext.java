package it.unibo.disi.spaf.samza;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.samza.application.EquivalentStreamApplication;
import it.unibo.disi.spaf.samza.application.EquivalentTaskApplication;
import joptsimple.OptionSet;
import org.apache.samza.application.SamzaApplication;
import org.apache.samza.runtime.ApplicationRunner;
import org.apache.samza.runtime.LocalApplicationRunner;
import org.apache.samza.util.CommandLine;
import scala.NotImplementedError;

public class SamzaContext implements Context {

	private final Config config;

	public SamzaContext(Config config) {
		super();
		this.config = config;
	}

	@Override
	public void run(Application application) {		
		boolean mapToStreamAPI = false;
		if (this.config.hasPath("samza.map-to-stream-api")) {
			mapToStreamAPI = this.config.getBoolean("samza.map-to-stream-api");
		}
		
		SamzaApplication<?> samzaApp;
		if (mapToStreamAPI) {
			samzaApp = new EquivalentStreamApplication(application);
		} else {
			samzaApp = new EquivalentTaskApplication(application);	
		}		

		// 1. Configuring and obtaining the execution context
		boolean isLocalEnvironment = false;
		if (this.config.hasPath("samza.local")) {
			isLocalEnvironment = this.config.getBoolean("samza.local");
		}
		
		String[] startArgs = {}; // TODO: make args mandatory?
		if (this.config.hasPath("samza.start-args")) {
			startArgs =  this.config.getStringList("samza.start-args").toArray(new String[0]);	
		}
		
		CommandLine cmdLine = new CommandLine();
		OptionSet options = cmdLine.parser().parse(startArgs);
		org.apache.samza.config.Config samzaConfig = cmdLine.loadConfig(options);
		
		ApplicationRunner runner;
		if (isLocalEnvironment) {
			runner = new LocalApplicationRunner(samzaApp, samzaConfig);
		} else {
			// TODO: implement Samza support for cluster deployment
			//runner = ApplicationRunners.getApplicationRunner(samzaApp, samzaConfig);
			throw new NotImplementedError("The samza cluster deployment support is not yet implemented.");
		}

		// 5. Application launch
		runner.run();
		runner.waitForFinish();
	}

}
