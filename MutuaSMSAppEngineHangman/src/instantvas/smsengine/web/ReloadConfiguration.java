package instantvas.smsengine.web;

import java.util.HashMap;

import config.InstantVASSMSEngineConfiguration;

public class ReloadConfiguration {

	public static byte[] process(HashMap<String, String> parameters, String queryString) {
		InstantVASSMSEngineConfiguration.loadConfiguration();
		return "RELOADED".getBytes();
	}
       
}
