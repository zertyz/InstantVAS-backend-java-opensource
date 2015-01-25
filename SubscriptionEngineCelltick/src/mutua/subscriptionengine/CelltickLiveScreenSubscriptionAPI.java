package mutua.subscriptionengine;

import java.io.IOException;

import mutua.icc.instrumentation.Instrumentation;
import adapters.HTTPClientAdapter;
import static mutua.subscriptionengine.ESubscriptionEngineInstrumentationProperties.*;
import static mutua.subscriptionengine.ESubscriptionEngineInstrumentationEvents.*;
import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;


/** <pre>
 * CelltickLiveScreenAPI.java
 * ==========================
 * (created by luiz, Jan 8, 2015)
 *
 * Manages Celltick LiveScreen API requests
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class CelltickLiveScreenSubscriptionAPI extends SubscriptionEngine {
	
	
	// configuration constants
	//////////////////////////
	
	public static String REGISTER_SUBSCRIBER_URL   = "http://localhost:8082/celltick/wapAPI?action=subpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";
	public static String UNREGISTER_SUBSCRIBER_URL = "http://localhost:8082/celltick/wapAPI?action=unsubpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1";


	public CelltickLiveScreenSubscriptionAPI(Instrumentation<?, ?> log) {
		super(log);
	}

	@Override
	public ESubscriptionOperationStatus subscribeUser(String userPhone,	String channelName) {
		String url = REGISTER_SUBSCRIBER_URL.replaceAll("%%MSISDN%%",  userPhone).
		                                     replaceAll("%%pkgname%%", channelName);
		try {
			String response = HTTPClientAdapter.requestGet(url, null, "UTF-8");
			if (response.indexOf("<id>100</id>") != -1) {
				log.reportEvent(SUBSCRIPTION_OK, REQUEST, url, RESPONSE, response);
				return ESubscriptionOperationStatus.OK;
			} else if (response.indexOf("<id>120</id>") != -1) {
				log.reportEvent(SUBSCRIPTION_ALREADY_SUBSCRIBED, REQUEST, url, RESPONSE, response);
				return ESubscriptionOperationStatus.ALREADY_SUBSCRIBED;
			} else {
				log.reportEvent(SUBSCRIPTION_AUTHENTICATION_ERROR, REQUEST, url, RESPONSE, response);
				return ESubscriptionOperationStatus.AUTHENTICATION_ERROR;
			}
		} catch (IOException e) {
			log.reportEvent(SUBSCRIPTION_COMMUNICATION_ERROR, REQUEST, url, DIP_THROWABLE, e);
			return ESubscriptionOperationStatus.COMMUNICATION_ERROR;
		}
	}

	@Override
	public EUnsubscriptionOperationStatus unsubscribeUser(String userPhone,	String channelName) {
		String url = UNREGISTER_SUBSCRIBER_URL.replaceAll("%%MSISDN%%", userPhone).
		                                       replaceAll("%%pkgname%%", channelName);
		try {
			String response = HTTPClientAdapter.requestGet(url, null, "UTF-8");
			if (response.indexOf("<id>100</id>") != -1) {
				log.reportEvent(UNSUBSCRIPTION_OK, REQUEST, url, RESPONSE, response);
				return EUnsubscriptionOperationStatus.OK;
			} else if (response.indexOf("<id>113</id>") != -1) {	// Packages not found for Subscriber
				log.reportEvent(UNSUBSCRIPTION_NOT_SUBSCRIBED, REQUEST, url, RESPONSE, response);
				return EUnsubscriptionOperationStatus.NOT_SUBSCRIBED;
			} else {
				log.reportEvent(UNSUBSCRIPTION_AUTHENTICATION_ERROR, REQUEST, url, RESPONSE, response);
				return EUnsubscriptionOperationStatus.AUTHENTICATION_ERROR;
			}
		} catch (IOException e) {
			log.reportEvent(UNSUBSCRIPTION_COMMUNICATION_ERROR, REQUEST, url, DIP_THROWABLE, e);
			return EUnsubscriptionOperationStatus.COMMUNICATION_ERROR;
		}
	}
}
