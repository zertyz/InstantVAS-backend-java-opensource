package mutua.smsappmodule.i18n.plugins;

import mutua.smsappmodule.i18n.plugins.UserGeoLocator.CountryStateByMSISDNResolver;

/** <pre>
 * IGeoLocatorPlaceHolder.java
 * ===========================
 * (created by luiz, May 28, 2016)
 *
 * Interface specifying geo locator plugins for the Profile Module
 *
 * @see CountryStateByMSISDNResolver
 * @version $Id$
 * @author luiz
 */

public interface IGeoLocatorPlaceHolder {
	
	/** Return the place holder name added to 'Profile' phrases by this plugin. For instance: the {@link CountryStateByMSISDNResolver} implementation
	 *  makes the {{countryStateByMSISDN}} place holder available, which aims to associate country state names to msisdns */
	String getPlaceHolderName();

	/** For occurrences 'Phrases' place holders named by {@link #getPlaceHolderName()} and having 'msisdn' as input, strive to determine the 'msisdn' location, returning it as a string.
	 *  For instance: the {@link CountryStateByMSISDNResolver} implementation returns the country state name */
	String getPlaceHolderValue(String msisdn);
}
