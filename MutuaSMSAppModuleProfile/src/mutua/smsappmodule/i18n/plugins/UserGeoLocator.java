package mutua.smsappmodule.i18n.plugins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** <pre>
 * UserGeoLocator.java
 * ===================
 * (created by luiz, May 25, 2016)
 *
 * A collection of Phrase plug-ins for telling where a user is from, based on several methods.
 * 
 * These plugins are not generic Phrase plugins like {@link IDynamicPlaceHolder} implementations. Instead, they are specific to the
 * profile module, adding a specific functionality (geo location), even though several methods of such funcionality may be implemented.
 *
 * @see IGeoLocatorPlaceHolder
 * @version $Id$
 * @author luiz
 */

public class UserGeoLocator {

	/** Based on a set of regular expressions to be matched against the user MSISDN, determine form which country state he/she is from.
	 *  Provides the {{countryStateByMSISDN}} place holder to Profile module phrases. */
	public static class CountryStateByMSISDNResolver implements IGeoLocatorPlaceHolder {
		
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
		public String getPlaceHolderName() {
			return "countryStateByMSISDN";
		}

		@Override
		public String getPlaceHolderValue(String msisdn) {
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
