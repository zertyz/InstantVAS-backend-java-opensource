package instantvas.smsengine.web;

import java.util.HashMap;

public class ReloadConfiguration {

	public byte[] process(HashMap<String, String> parameters, String queryString) {
//		InstantVASSMSEngineConfiguration.loadConfiguration();
		return "RELOADED".getBytes();
	}
       
}
