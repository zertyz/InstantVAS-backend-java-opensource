package mutua.smsappmodule.i18n.plugins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;

/** <pre>
 * UserGeoLocator.java
 * ===================
 * (created by luiz, May 25, 2016)
 *
 * A plug-in for {@link SMSAppModulePhrasingsProfile} adding the functionality of 
 * telling where a user is from, based on several methods
 *
 * @see IDynamicPlaceHolder
 * @version $Id$
 * @author luiz
 */

public class UserGeoLocator {

	/** Based on a set of regular expressions to be matched against the user MSISDN, determine form which country state he/she is from */
	public static class CountryStateByMSISDNResolver implements IDynamicPlaceHolder {
		
		private String[]  stateNames;
		private Pattern[] msisdnPatterns;
		private String    unmatchedMSISDNStateName;
		
		/** Constructs an instance from a String array -- as it will come from an InstantVASInstance configuration file
		 *  'regexResolverList' := {StateName1, MSISDNPattern1, ..., UnmatchedMSISDNStateName} */
		public CountryStateByMSISDNResolver(String[] regexResolverList) {
			unmatchedMSISDNStateName = regexResolverList[regexResolverList.length-1];
			stateNames    = new String[regexResolverList.length / 2];
			msisdnPatterns = new Pattern[regexResolverList.length / 2];
			for (int i=0; i<stateNames.length; i++) {
				stateNames[i]     = regexResolverList[i*2];
				msisdnPatterns[i] = Pattern.compile(regexResolverList[i*2+1]);
			}
		}

		@Override
		// TODO 20160525 -- MSISDN should not be passed on a phrase parameter. It should be accessible to plugins in the same way sessions,
		//      databases, etc, would be -- making this a really uncoupled plug-in architecture. A solution for this is not yet tought.
		public String getPlaceHolderValue(String placeHolderName, String[] phraseParameters) {
			String msisdn = null; 
			for (int i=0; i<phraseParameters.length; i+=2) {
				if ("MSISDN".equals(phraseParameters[i])) {
					msisdn = phraseParameters[i+1];
				}
			}
			if (msisdn == null) {
				throw new RuntimeException("'CountryStateByMSISDNResolver' currently requires phrases to provide a parameter named 'MSISDN'");
			}
			for (int i=0; i<msisdnPatterns.length; i++) {
				Matcher m = msisdnPatterns[i].matcher(msisdn);
				if (m.matches()) {
					return stateNames[i];
				}
			}
			return unmatchedMSISDNStateName;
		}
		
	}
}
