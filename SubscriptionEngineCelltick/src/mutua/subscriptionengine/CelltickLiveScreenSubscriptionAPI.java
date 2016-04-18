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
	
	
	private final String liveScreenServiceBaseUrl; // = "http://localhost:8082/celltick/wapAPI // ?action=(un)subpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1"
	private final String channelName;
	
	private final HTTPClientAdapter lifecycleClient;


	public CelltickLiveScreenSubscriptionAPI(Instrumentation<?, ?> log, String liveScreenServiceBaseUrl, String channelName) {
		super(log);
		this.liveScreenServiceBaseUrl   = liveScreenServiceBaseUrl;
		this.channelName = channelName;
		HTTPClientAdapter.configureDefaultValuesForNewInstances(-1, -1, false, "User-Agent", "InstantVAS.com lifecycle client");
		lifecycleClient = new HTTPClientAdapter(liveScreenServiceBaseUrl);
	}

	@Override
	public ESubscriptionOperationStatus subscribeUser(String userPhone) {
		
		String[] request = {
			"action",  "subpkg",
			"msisdn",  userPhone,
			"pkgname", channelName,
			"charge",  "1"};
		
		try {
			String response = lifecycleClient.requestGet(request);
			if (response.indexOf("<id>100</id>") != -1) {
				log.reportEvent(SUBSCRIPTION_OK, BASE_URL, liveScreenServiceBaseUrl, REQUEST, request, RESPONSE, response);
				return ESubscriptionOperationStatus.OK;
			} else if (response.indexOf("<id>120</id>") != -1) {
				log.reportEvent(SUBSCRIPTION_ALREADY_SUBSCRIBED, BASE_URL, liveScreenServiceBaseUrl, REQUEST, request, RESPONSE, response);
				return ESubscriptionOperationStatus.ALREADY_SUBSCRIBED;
			} else {
				log.reportEvent(SUBSCRIPTION_AUTHENTICATION_ERROR, BASE_URL, liveScreenServiceBaseUrl, REQUEST, request, RESPONSE, response);
				return ESubscriptionOperationStatus.AUTHENTICATION_ERROR;
			}
		} catch (IOException e) {
			log.reportEvent(SUBSCRIPTION_COMMUNICATION_ERROR, BASE_URL, liveScreenServiceBaseUrl, REQUEST, request, DIP_THROWABLE, e);
			return ESubscriptionOperationStatus.COMMUNICATION_ERROR;
		}
	}

	@Override
	public EUnsubscriptionOperationStatus unsubscribeUser(String userPhone) {

		String[] request = {
			"action",  "unsubpkg",
			"msisdn",  userPhone,
			"pkgname", channelName,
			"charge",  "1"};
		
		try {
			String response = lifecycleClient.requestGet(request);
			if (response.indexOf("<id>100</id>") != -1) {
				log.reportEvent(UNSUBSCRIPTION_OK, BASE_URL, liveScreenServiceBaseUrl, REQUEST, request, RESPONSE, response);
				return EUnsubscriptionOperationStatus.OK;
			} else if (response.indexOf("<id>113</id>") != -1) {	// Packages not found for Subscriber
				log.reportEvent(UNSUBSCRIPTION_NOT_SUBSCRIBED, BASE_URL, liveScreenServiceBaseUrl, REQUEST, request, RESPONSE, response);
				return EUnsubscriptionOperationStatus.NOT_SUBSCRIBED;
			} else {
				log.reportEvent(UNSUBSCRIPTION_AUTHENTICATION_ERROR, BASE_URL, liveScreenServiceBaseUrl, REQUEST, request, RESPONSE, response);
				return EUnsubscriptionOperationStatus.AUTHENTICATION_ERROR;
			}
		} catch (IOException e) {
			log.reportEvent(UNSUBSCRIPTION_COMMUNICATION_ERROR, BASE_URL, liveScreenServiceBaseUrl, REQUEST, request, DIP_THROWABLE, e);
			return EUnsubscriptionOperationStatus.COMMUNICATION_ERROR;
		}
	}
}
